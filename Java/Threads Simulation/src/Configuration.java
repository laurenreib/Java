import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Configure the file from a txt file and create a Sensor network from text
 * files
 */
public class Configuration {
    /**
     * Internal book keeping to temporarily pass in data for the Collection
     * data types in buildSensor
     */
    private static class MiniNode {
        public int row;
        public int col;
        public MiniNode (int row, int col) {
            this.row = row;
            this.col = col;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MiniNode that = (MiniNode) o;
            return this.row == that.row && this.col == that.col;
        }

        @Override
        public int hashCode() {
            return 31 * row + col;
        }
    }

    /**
     * Main method
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        String path = args.length >= 1 ? args[0] : "No argument";
        Sensor sensor = null;
        if(path.equals("No argument")) {
            throw new IllegalArgumentException("No argument given");
        } else {
            sensor = buildSensor("example_config/" + path);
            System.out.println(sensor);
            MarkForNegation.markSensorsForDeletion(sensor, new HashSet<>());
        }
    }

    /**
     * Method to take in the configuration file and then parse it line by line
     * builds the Sensor and then returns the Sensor
     * @param path File path probably taken in from Console
     * @return Sensor built from a txt file
     */
    public static Sensor buildSensor(String path) {
        Map<MiniNode, Sensor> nodes = new HashMap<>();
        List<MiniNode[]> edges = new ArrayList<>();
        MiniNode stationLocation = null;
        MiniNode fireLocation = null;

        try (InputStream in = Files.newInputStream(Path.of(path));
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;

                String[] values = line.split("\\s+");
                int row, col;

                switch (values[0]) {
                    case "node":
                        row = Integer.parseInt(values[1]);
                        col = Integer.parseInt(values[2]);
                        MiniNode nodeKey = new MiniNode(row, col);
                        nodes.put(nodeKey, new Sensor(row, col,
                                SensorStatus.NORMAL));
                        break;

                    case "edge":
                        int row1 = Integer.parseInt(values[1]);
                        int col1 = Integer.parseInt(values[2]);
                        int row2 = Integer.parseInt(values[3]);
                        int col2 = Integer.parseInt(values[4]);
                        edges.add(new MiniNode[]
                                {new MiniNode(row1, col1),
                                new MiniNode(row2, col2)});
                        break;

                    case "station":
                        row = Integer.parseInt(values[1]);
                        col = Integer.parseInt(values[2]);
                        stationLocation = new MiniNode(row, col);
                        break;

                    case "fire":
                        row = Integer.parseInt(values[1]);
                        col = Integer.parseInt(values[2]);
                        fireLocation = new MiniNode(row, col);
                        break;

                    default:
                        System.out.println("Unrecognized line in file: "
                                + line);
                        return null;
                }
            }
        } catch (IOException e) {
            System.err.println("Error! File: " + path + " was not found!");
            return null;
        }

        // Check to see if the text file properly built what we specified
        if(stationLocation == null) {
            System.out.println("Your text file is incorrect");
            return null;
        } else if (fireLocation == null) {
            System.out.println("Your text file is incorrect");
            return null;
        } else if (edges.isEmpty()) {
            System.out.println("Your text file is incorrect");
            return null;
        } else if (nodes.isEmpty()) {
            System.out.println("Your text file is incorrect");
            return null;
        }

        // Connect all sensors
        for (MiniNode[] edge : edges) {
            Sensor a = nodes.get(edge[0]);
            Sensor b = nodes.get(edge[1]);
            Sensor c = nodes.get(fireLocation);
            if (a != null && b != null) {
                a.add(b);
            }
        }
        nodes.get(stationLocation).setStatus(SensorStatus.BASE_STATION);
        nodes.get(stationLocation).setFireLocation(nodes.get(fireLocation));
        nodes.get(fireLocation).setStatus(SensorStatus.BURNING);
        return nodes.get(stationLocation);
    }
}
