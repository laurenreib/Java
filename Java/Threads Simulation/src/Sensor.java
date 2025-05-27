import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;

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
    // Allow only one mobile agent here at a time
    private final Semaphore lock = new Semaphore(1);
    private volatile MobileAgent agentHere;

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
    public synchronized int getRow() {
        return this.node.row;
    }

    /**
     * Getter for the current Sensor's column value
     *
     * @return value of the column
     */
    public synchronized int getCol() {
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

        Platform.runLater(() -> {
            if(FireSimulationGUI.gui != null) {
                FireSimulationGUI.gui.changeColor(this.getRow(), this.getCol(),
                        status);
            }
        });

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

   /**
    * Handles propagation when an update is received from a neighbor.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof String)) return;

        String message = (String) arg;
        String[] args = message.split("\\s+");

        switch (args[0]) {
            case SensorStatus.BURNING:
                // Only allow fire to spread after GUI button is pressed
                if (FireSimulationGUI.fireStarted &&
                        (this.getStatus().equals(SensorStatus.NORMAL) || this.getStatus().equals(SensorStatus.BASE_STATION))) {

                    // Check: if this is BASE_STATION, only allow if all neighbors are burning or dead
                    if (this.getStatus().equals(SensorStatus.BASE_STATION)) {
                        boolean surrounded = true;
                        for (Node neighbor : node.getPtrs()) {
                            String nStatus = neighbor.getOwner().getStatus();
                            if (!nStatus.equals(SensorStatus.BURNING) && !nStatus.equals(SensorStatus.DEAD)) {
                                surrounded = false;
                                break;
                            }
                        }

                        if (!surrounded) return;  // Not ready to burn yet
                    }

                    // Standard fire progression
                    this.setStatus(SensorStatus.YELLOW);
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


    /**
     * Execution loop for burning node to spread fire and become DEAD
      */
    @Override
    public void run() {
        if (!manuallyActivated) return;  // Only run if manually activated

        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Allow BASE_STATION to burn if it's manually activated
                setStatus(SensorStatus.BURNING);

                for (Node neighbor : node.getPtrs()) {
                    Sensor neighborSensor = neighbor.getOwner();
                    if (neighborSensor.getStatus().equals(SensorStatus.NORMAL)) {
                        neighborSensor.update(this, SensorStatus.BURNING + " " + getRow() + " " + getCol());
                    }
                }

                // If all neighbors are dead or burning, mark self as DEAD
                boolean allBurned = true;
                for (Node neighbor : node.getPtrs()) {
                    String status = neighbor.getOwner().getStatus();
                    if (!status.equals(SensorStatus.BURNING) && !status.equals(SensorStatus.DEAD)) {
                        allBurned = false;
                        break;
                    }
                }

                if (allBurned) {
                    setStatus(SensorStatus.DEAD);
                    System.out.println("Sensor at (" + getRow() + "," + getCol() + ") is now DEAD");
                    return;
                }

                Thread.sleep((long) (FireSimulationGUI.fireSpeed * 1000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Starts fire simulation from this sensor
     */
    public void activate() {
        this.manuallyActivated = true;
        System.out.println("Sensor at (" + getRow() + "," + getCol() + ") manually activated.");

        // Kick off fire propagation by sending a BURNING update to itself
        this.update(this, SensorStatus.BURNING + " " + getRow() + " " + getCol());

        // Start the run loop
        new Thread(this).start();
    }
    public boolean occupySpace (MobileAgent agent) {
        if(getStatus().equals(SensorStatus.BURNING) ||
                getStatus().equals(SensorStatus.DEAD)) {
            return false;
        }
        if(lock.tryAcquire()) {
            try {
                if (this.agentHere == null) {
                    this.agentHere = agent;
                    return true;
                } else {
                    return false;
                }
            } finally {
                if (this.agentHere != agent) {
                    lock.release();
                }
            }
        } else {
            return false;
        }
    }
    public void releaseSpace () {
        // This part basically ensures that we are able to safely perform the
        // lock.release() call because we have to we release safely "no
        // matter what"
        lock.acquireUninterruptibly();
        try {
            this.agentHere = null;
        } finally {
            lock.release();
        }
    }
}