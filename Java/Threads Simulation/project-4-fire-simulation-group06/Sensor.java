import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Sensor represents the sensor/nodes to build the network of various
 * pathways in the forest
 */
public class Sensor extends Observable implements Observer, Runnable {
    /**
     * Status of the current Sensor:
     * i.e. BURNING, NOT BURNING
     */
    private final BlockingQueue<String> requests;
    private volatile String status;
    private Sensor fireLocation;
    private volatile boolean manuallyActivated = false;

    /**
     * Inner class for pointer book-keeping
     */
    public class Node {
        private final Set<Node> ptrs;
        private int row;
        private int col;
        private Sensor owner;

        /**
         * Create a new Node to point to other Nodes within Sensor
         *
         * @param col Column value
         * @param row Row value
         */
        public Node(int row, int col, Sensor sensor) {
            this.ptrs = Collections.synchronizedSet(new HashSet<>());
            this.row = row;
            this.col = col;
            this.owner = sensor;
        }

        /**
         * Getter for the pointer values for this Node
         *
         * @return Set of Pointers of Node type
         */
        public Set<Node> getPtrs() {
            return this.ptrs;
        }

        /**
         * Get the owner of this Sensor
         *
         * @return returns the Sensor that this Node belongs to
         */
        public Sensor getOwner() {
            return this.owner;
        }

        /**
         * Overriding equals to create an effective Set management from HashSet
         *
         * @param o Another Object that's hopefully a Node
         * @return Whether the Node is equal to another Node
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return row == node.row && col == node.col;
        }

        /**
         * Overriding hashCode to create an effective Set management from
         * HashSet
         *
         * @return HashCode of the values kept by row and col
         */
        @Override
        public int hashCode() {
            return 31 * row + col;
        }
    }

    public Node node;

    /**
     * Constructor for Sensor
     */
    public Sensor(int row, int col, String status) {
        this.node = new Node(row, col, this);
        this.status = status;
        this.requests = new LinkedBlockingQueue<>();
    }

    /**
     * Add a Sensor and point it to another Sensor
     *
     * @return True if successfully added to the pointers
     */
    public boolean add(Sensor s) {
        // Prevent self-links
        if (this == s) {
            return false;
        }
        if (this.node.ptrs.add(s.node)) {
            s.node.ptrs.add(this.node);
            this.addObserver(s);
            s.addObserver(this);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Getter for status for this sensor
     *
     * @return status for this sensor
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Getter for the current Sensor's row value
     *
     * @return value of the row
     */
    public int getRow() {
        return this.node.row;
    }

    /**
     * Getter for the current Sensor's column value
     *
     * @return value of the column
     */
    public int getCol() {
        return this.node.col;
    }

    /**
     * More general version for toString to just straight up override it and
     * call the recursive toString
     *
     * @return the Sensor in String form
     */
    @Override
    public String toString() {
        return toString(this, Collections.synchronizedSet(new HashSet<>()));
    }

    /**
     * Use recursion to add each node and it's elements to a String
     *
     * @param s       the Sensor
     * @param visited Set of Sensors that have been visited
     * @return String form of Sensor
     */
    public String toString(Sensor s, Set<Sensor> visited) {
        String result = "";

        // Double check to see if we've already deleted the current Sensor
        // being traversed
        if (s == null || visited.contains(s)) {
            return "";
        }
        visited.add(s);
        result += "Row: " + s.getRow() + " Col: " + s.getCol() + "\n";
        result += "\tStatus: " + s.getStatus() + "\n";
        // Use recursion to traverse and print out all the values of Sensor
        // Also print out the links it has
        synchronized (s.node.ptrs) {
            for (Sensor.Node node : s.node.ptrs) {
                result += "\tLinked to: " + node.owner.getRow() + " " +
                        node.owner.getCol() + "\n";
            }
            for (Sensor.Node node : s.node.ptrs) {
                result += toString(node.owner, visited);
            }
        }
        return result;
    }

    /**
     * Setter for status member
     *
     * @param status the status of the current sensor
     */
    public synchronized void setStatus(String status) {
        this.status = status;
        setChanged();
        String message = status + " " + this.getRow() + " " + this.getCol();
        notifyObservers(message);
    }

    /**
     * Set the fire location
     *
     * @param location
     */
    public void setFireLocation(Sensor location) {
        this.fireLocation = location;
    }

    /**
     * Get the location where the fire started
     *
     * @return
     */
    public Sensor getFireLocation() {
        return this.fireLocation;
    }

    //NEWLY ADDED
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String message = (String) arg;
            String[] args = message.split("\\s+");
            switch (args[0]) {
                case SensorStatus.BURNING:
                    if (this.getStatus().equals(SensorStatus.NORMAL)) {
                        this.setStatus(SensorStatus.YELLOW);
                        // Add delay before turning to BURNING
                        new Thread(() -> {
                            try {
                                Thread.sleep((long) (FireSimulationGUI.fireSpeed * 1000));
                                if (this.getStatus().equals(SensorStatus.YELLOW)) {
                                    this.setStatus(SensorStatus.BURNING);
                                    new Thread(this).start();
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                    }
                    break;
            }
        }
    }

    //NEWLEY ADDED
    @Override
    public void run() {
        if (!manuallyActivated) return;  // Only run if manually activated

        try {
            while (!Thread.currentThread().isInterrupted()) {
                setStatus(SensorStatus.BURNING);

                // Spread to neighbors just once per iteration
                for (Node neighbor : node.getPtrs()) {
                    if (neighbor.getOwner().getStatus().equals(SensorStatus.NORMAL)) {
                        neighbor.getOwner().update(this, SensorStatus.BURNING + " " +
                                this.getRow() + " " + this.getCol());
                    }
                }

                // Check if all neighbors are burning/dead
                boolean allBurned = true;
                for (Node neighbor : node.getPtrs()) {
                    String status = neighbor.getOwner().getStatus();
                    if (!status.equals(SensorStatus.BURNING) &&
                            !status.equals(SensorStatus.DEAD)) {
                        allBurned = false;
                        break;
                    }
                }

                if (allBurned) {
                    setStatus(SensorStatus.DEAD);
                    System.out.println(this + " completely burned out");
                    return;  // Exit thread when done
                }

                Thread.sleep((long) (FireSimulationGUI.fireSpeed * 1000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void activate() {
        this.manuallyActivated = true;
        new Thread(this).start();
    }
}