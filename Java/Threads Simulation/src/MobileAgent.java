import java.util.*;

public class MobileAgent extends Observable implements Runnable, Observer {
    private static int idCounter = 0;
    private final int agentId;
    private Sensor currentSensor;
    private final BaseStation base;
    private boolean isAlive;
    private static final Set<Sensor> visitedNodes = Collections.synchronizedSet(new HashSet<>());
    private Queue<Sensor> movementQueue = new LinkedList<>();

    public MobileAgent(Sensor start, BaseStation base) {
        this.currentSensor = start;
        currentSensor.addObserver(this);
        FireSimulationGUI.gui.observeMobileAgent(this);
        this.base = base;
        this.agentId = base.generateId();
        this.isAlive = true;

        base.logAgent(this);
    }
    public synchronized BaseStation getBase() {
        return this.base;
    }

    /**
     * Getter for agent ID
     * @return agent ID
     */
    public synchronized int getAgentId() {
        return this.agentId;
    }

    /**
     * Getter for current sensor
     * @return current sensor
     */
    public synchronized Sensor getCurrentSensor() {
        return currentSensor;
    }
    public boolean isAlive() {
        return isAlive;
    }

    public void die() {
        isAlive = false;

        setChanged();
        notifyObservers(StationMessages.DEATH + " " + getAgentId());
        setChanged();
        notifyObservers("Agent " + (isAlive() ? "Alive":
                "Dead") + " " + (double)getCurrentSensor().getRow()
                + " " + (double)getCurrentSensor().getCol());
    }
    private void spreadToNeighbors(Sensor sensor) {
        if (visitedNodes.contains(sensor)) {
            System.out.println("Agent " + agentId + " skipping already visited node at [" +
                    sensor.getCol() + "," + sensor.getRow() + "]");
            return;
        }

        visitedNodes.add(sensor);

        for (Sensor.Node neighborNode : sensor.node.getPtrs()) {
            Sensor neighbor = neighborNode.getOwner();

            if (neighbor != null &&
                    !SensorStatus.BURNING.equalsIgnoreCase(neighbor.getStatus()) &&
                    !visitedNodes.contains(neighbor)) {
                // Attempt to occupy space, if done successfully alerts the
                // base station of it
                if (neighbor.occupySpace(this)) {
                    MobileAgent clone = new MobileAgent(neighbor, getBase());
                    new Thread(clone).start();
                    System.out.println("Agent " + agentId + " cloned new " +
                            "agent at [" + neighbor.getCol() + "," +
                            neighbor.getRow() + "]");
                }
            }
        }
    }

    /**
     * Makes observations based on the Sensor (currentSensor) that it's
     * observing
     * @param o     the observable object.
     * @param arg   an argument passed to the {@code notifyObservers}
     *                 method.
     */
    @Override
    public void update(Observable o, Object arg) {
        // If it's dead can't update
        if(!isAlive) {
            return;
        }
        if (arg instanceof String) {
            String message = (String) arg;
            String[] args = message.split("\\s+");

            switch (args[0]) {
                case SensorStatus.YELLOW:
                    spreadToNeighbors(getCurrentSensor());
                    break;
                case SensorStatus.BURNING:
                    die();
                    break;
                default:
                    System.out.println(StationMessages.ERROR_MESSAGE);
            }
        }
    }
    @Override
    public void run() {
        setChanged();
        notifyObservers(StationMessages.DISPATCH + " " + getAgentId() + " "
                + (double)getCurrentSensor().getRow() + " " + (double)getCurrentSensor().getCol());
        while (isAlive) {
            String status = currentSensor.getStatus();
            // TODO: Check to see if the rest of the run logic would really
            //  be needed since observers seem to take care of everything
            //  well logically at least

            setChanged();
            notifyObservers("Agent " + (isAlive() ? "Alive":
                    "Dead") + " " + (double)getCurrentSensor().getRow()
                            + " " + (double)getCurrentSensor().getCol());
            try {
                Thread.sleep((long)(50 * FireSimulationGUI.fireSpeed));
            } catch (InterruptedException e ) {
                die();
            }
//            List<Sensor.Node> neighbors = new ArrayList<>(currentSensor.node.getPtrs());
//            if (neighbors.isEmpty()) {
//                System.out.println("Agent " + agentId + " found no neighbors at [" +
//                        currentSensor.getCol() + "," + currentSensor.getRow() + "]");
//                return;
//            }
//
//            Sensor.Node next = neighbors.get(new Random().nextInt(neighbors.size()));
//            if (next.getOwner() != null) {
//                movementQueue.add(next.getOwner());
//                System.out.println("Agent " + agentId + " queued move to [" +
//                        next.getOwner().getRow() + "," + next.getOwner().getCol() + "]");
//            }
//
//            if (!movementQueue.isEmpty()) {
//                currentSensor = movementQueue.poll();
//                System.out.println("Agent " + agentId + " moved to [" +
//                        currentSensor.getRow() + "," + currentSensor.getCol() +
//                        "]");
//            }
        }
    }
}
