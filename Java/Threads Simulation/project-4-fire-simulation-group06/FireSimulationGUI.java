import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.util.Duration;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FireSimulationGUI extends Application implements Observer {
    private static Sensor Network;
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 800;
    private static final double NODE_RADIUS = 10;
    private static final double GRID_SPACING = 85.0;

    private static Map<String, Circle> nodes = new HashMap<>();
    private static Set<String> fireNodes = new HashSet<>();
    private static double xOffset = 0;
    private static double yOffset = 0;

    private Timeline fireTimeline;
    public static double fireSpeed = 1.0; // Default speed
    private static final Queue<String> updates = new ConcurrentLinkedQueue<>();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane simulationPane = new Pane();
        String filename = "sample.txt"; // Change this if needed
//        loadConfig("example_config/" + filename, simulationPane);
        try {
            File configFile = new File("example_config/" + filename);
            Scanner scanner = new Scanner(configFile);

            List<int[]> nodeCoords = new ArrayList<>();
            List<String> configLines = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                configLines.add(line);

                if (line.startsWith("node") || line.startsWith("station") || line.startsWith("fire")) {
                    String[] parts = line.split(" ");
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    nodeCoords.add(new int[]{x, y});
                } else if (line.startsWith("edge")) {
                    String[] parts = line.split(" ");
                    nodeCoords.add(new int[]{Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2])});
                    nodeCoords.add(new int[]{Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4])});
                }
            }

            // Calculate bounding box
            int minX = nodeCoords.stream().mapToInt(c -> c[0]).min().orElse(0);
            int maxX = nodeCoords.stream().mapToInt(c -> c[0]).max().orElse(0);
            int minY = nodeCoords.stream().mapToInt(c -> c[1]).min().orElse(0);
            int maxY = nodeCoords.stream().mapToInt(c -> c[1]).max().orElse(0);

            double gridWidth = (maxX - minX + 1) * GRID_SPACING;
            double gridHeight = (maxY - minY + 1) * GRID_SPACING;
            xOffset = (WINDOW_WIDTH - gridWidth) / 2 - minX * GRID_SPACING;
            yOffset = (WINDOW_HEIGHT - gridHeight) / 2 - minY * GRID_SPACING;
        } catch (Exception e) {
            System.out.println("file not found!");
        }

        Network = Configuration.buildSensor("example_config/" + filename);
        showNetwork(simulationPane, Network,
                Collections.synchronizedSet(new HashSet<>()));
        observeEachNode(Network, this, Collections.synchronizedSet(new HashSet<>()));
        // Create Start Fire Button
        Button startButton = new Button("Start Fire");
        startButton.setLayoutX(20);
        startButton.setLayoutY(20);
        startButton.setOnAction(event -> {
            // Clear previous fire
            fireNodes.clear();
            updates.clear();

            // Manually start only the initial fire location
            Sensor fireLoc = Network.getFireLocation();
            if (fireLoc != null) {
                new Thread(() -> {
                    fireLoc.setStatus(SensorStatus.BURNING);
                }).start();
            }

            // Start the visualization timeline
            startFirePropagation(simulationPane);
        });

        // Create Slider for adjusting fire speed
        Slider speedSlider = new Slider(0.1, 5.0, 1.0);
        speedSlider.setLayoutX(20);
        speedSlider.setLayoutY(60);
        speedSlider.setBlockIncrement(0.1);
        speedSlider.setMajorTickUnit(1.0);
        speedSlider.setMinorTickCount(10);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);

        // Listener to adjust fire speed
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fireSpeed = newValue.doubleValue();
            if (fireTimeline != null && fireTimeline.getStatus() == Timeline.Status.RUNNING) {
                fireTimeline.setRate(fireSpeed); // Adjust the rate of fire propagation
            }
        });

        // Add controls to the simulation pane
        simulationPane.getChildren().addAll(startButton, speedSlider);

        StackPane root = new StackPane(simulationPane);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("Forest Fire Monitoring Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event-> {
            try {
                MarkForNegation.
                        markSensorsForDeletion(Network,
                                Collections.synchronizedSet(new HashSet<>()));
                System.out.println("Successfully cleaned");
            } catch (Exception e) {
                System.out.println("Unable to clean up after");
            }
        });
    }

    private void showNetwork (Pane simulationPane, Sensor s,
                              Set<Sensor> visited) {
        if(s == null || visited.contains(s)) {
            return;
        }
        visited.add(s);

        for(Sensor.Node n : s.node.getPtrs()) {
            double x1 = s.getRow() * GRID_SPACING + xOffset;
            double y1 = s.getCol() * GRID_SPACING + yOffset;
            double x2 = n.getOwner().getRow() * GRID_SPACING + xOffset;
            double y2 = n.getOwner().getCol() * GRID_SPACING + yOffset;
            Line edge = createEdge(x1, y1, x2, y2);
            simulationPane.getChildren().add(edge);
            showNetwork(simulationPane, n.getOwner(), visited);
        }

        Color color = switch (s.getStatus()) {
            case SensorStatus.BASE_STATION -> Color.GREEN.brighter();
            case SensorStatus.NORMAL -> Color.BLUE;
            case SensorStatus.BURNING -> Color.RED;
            case SensorStatus.YELLOW -> Color.YELLOW;
            case SensorStatus.DEAD -> Color.RED.darker();
            default -> Color.BLACK;
        };

        if(s.getStatus().equals(SensorStatus.BURNING)) {
            fireNodes.add(s.getRow() + "," + s.getCol());
        }
        double x = s.getRow() * GRID_SPACING + xOffset;
        double y = s.getCol() * GRID_SPACING + yOffset;
        Circle node = createNode(x, y, color);
        nodes.put(x + "," + y, node);

        simulationPane.getChildren().add(node);
    }
    public static void addFireNode (double x, double y) {
        x = x * GRID_SPACING + xOffset;
        y = y * GRID_SPACING + yOffset;
        updates.offer("Burned " + x + " " + y);
        System.out.println("Burned " + x + " " + y);
    }
    /**
     * String is the key that will be used to find the corresponding circle
     */
    public static synchronized void changeColor (double x, double y, String color) {
        Color colour = switch (color) {
            case SensorStatus.BASE_STATION -> Color.GREEN.brighter();
            case SensorStatus.NORMAL -> Color.BLUE;
            case SensorStatus.BURNING -> Color.RED;
            case SensorStatus.YELLOW -> Color.YELLOW;
            case SensorStatus.DEAD -> Color.RED.darker();
            default -> Color.BLACK;
        };
        x = x * GRID_SPACING + xOffset;
        y = y * GRID_SPACING + yOffset;
        nodes.get(x + "," + y).setFill(colour);
    }

    private Circle createNode(double x, double y, Color color) {
        Circle circle = new Circle(x, y, NODE_RADIUS);
        circle.setFill(color);
        circle.setStroke(Color.BLACK);
        return circle;
    }

    private Line createEdge(double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(Color.BLACK);
        return line;
    }


    private void startFirePropagation(Pane simulationPane) {
        if (fireTimeline != null) {
            fireTimeline.stop();
        }

        fireTimeline = new Timeline(
                new KeyFrame(Duration.millis(200), event -> {
                    // Process only one update per frame
                    String update = updates.poll();
                    if (update != null) {
                        Platform.runLater(() -> {
                            String[] args = update.split("\\s+");
                            if (args[0].equals(SensorStatus.BURNING)) {
                                double x = Double.parseDouble(args[1]);
                                double y = Double.parseDouble(args[2]);
                                changeColor(x, y, SensorStatus.BURNING);
                            }
                        });
                    }
                }));
        fireTimeline.setCycleCount(Timeline.INDEFINITE);
        fireTimeline.play();
    }

    private void spreadFire(Pane simulationPane) {
        Set<String> newFireNodes = new HashSet<>();

        for (String fireNodeKey : fireNodes) {
            String[] coords = fireNodeKey.split(",");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);

            for (String neighborKey : nodes.keySet()) {
                if (fireNodes.contains(neighborKey)) continue;

                String[] neighborCoords = neighborKey.split(",");
                double nx = Double.parseDouble(neighborCoords[0]);
                double ny = Double.parseDouble(neighborCoords[1]);

                if (Math.abs(nx - x) <= GRID_SPACING && Math.abs(ny - y) <= GRID_SPACING) {
                    Circle fireParticle = createNode(nx, ny, Color.RED);
                    simulationPane.getChildren().add(fireParticle);
                    newFireNodes.add(neighborKey);
                }
            }
        }

        fireNodes.addAll(newFireNodes);
    }
    private synchronized void observeEachNode (Sensor sensor, Observer observer,
                                               Set<Sensor> visited) {
        if(visited.contains(sensor) || sensor == null) {
            return;
        }
        visited.add(sensor);
        sensor.addObserver(observer);
        for (Sensor.Node s : sensor.node.getPtrs()) {
            observeEachNode(s.getOwner(), observer, visited);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        synchronized (this) {
            if (arg instanceof String) {
                String[] args = ((String) arg).split("\\s+");
                System.out.println((String) arg);
                double x, y;
                switch (args[0]) {
                    case SensorStatus.BURNING:
                        x = Double.parseDouble(args[1]);
                        y = Double.parseDouble(args[2]);
                        changeColor(x, y, SensorStatus.BURNING);
                        break;
                    case SensorStatus.YELLOW:
                        x = Double.parseDouble(args[1]);
                        y = Double.parseDouble(args[2]);
                        changeColor(x, y, SensorStatus.YELLOW);
                        break;
                    case SensorStatus.BASE_STATION:
                        x = Double.parseDouble(args[1]);
                        y = Double.parseDouble(args[2]);
                        Platform.runLater(() -> {
                            changeColor(x, y,
                                    SensorStatus.BASE_STATION);
                        });
                        break;
                }
            }
        }
    }
}