import java.util.*;

public class MobileAgent implements Runnable {
    private static int idCounter = 0;
    private final int agentId;
    private Sensor currentSensor;
    private final BaseStation base;
    private boolean isAlive;
    private static final Set<Sensor> visitedNodes = Collections.synchronizedSet(new HashSet<>());
    private Queue<Sensor> movementQueue = new LinkedList<>();

    public MobileAgent(Sensor start, BaseStation base) {
        this.currentSensor = start;
        this.base = base;
        this.agentId = generateId();
        this.isAlive = true;

        base.logAgent(String.valueOf(agentId), start);
        System.out.println("Agent " + agentId + " initialized at [" + start.getCol() + ", " + start.getRow() + "]");
    }

    private synchronized int generateId() {
        return ++idCounter;
    }

    public void run() {
        while (isAlive) {
            String status = currentSensor.getStatus();
            System.out.println("Agent " + agentId + " at [" + currentSensor.getCol() + ", " + currentSensor.getRow() + "] - Status: " + status);

            if ("Burning".equalsIgnoreCase(status)) {
                System.out.println("Agent " + agentId + " died on fire at [" +
                        currentSensor.getCol() + "," + currentSensor.getRow() + "]");
                isAlive = false;
                return;
            }

            if ("YELLOW".equalsIgnoreCase(status)) {
                System.out.println("Agent " + agentId + " detected fire at [" +
                        currentSensor.getCol() + "," + currentSensor.getRow() + "] - Spreading to neighbors.");
                spreadToNeighbors(currentSensor);
                isAlive = false;
                return;
            }

            List<Sensor.Node> neighbors = new ArrayList<>(currentSensor.node.getPtrs());
            if (neighbors.isEmpty()) {
                System.out.println("Agent " + agentId + " found no neighbors at [" +
                        currentSensor.getCol() + "," + currentSensor.getRow() + "]");
                return;
            }

            Sensor.Node next = neighbors.get(new Random().nextInt(neighbors.size()));
            if (next.getOwner() != null) {
                movementQueue.add(next.getOwner());
                System.out.println("Agent " + agentId + " queued move to [" +
                        next.getOwner().getRow() + "," + next.getOwner().getCol() + "]");
            }

            if (!movementQueue.isEmpty()) {
                currentSensor = movementQueue.poll();
                System.out.println("Agent " + agentId + " moved to [" +
                        currentSensor.getRow() + "," + currentSensor.getCol() +
                        "]");
            }

            try {
                Thread.sleep(1000);  // 1 second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
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
                    !"Burning".equalsIgnoreCase(neighbor.getStatus()) &&
                    !visitedNodes.contains(neighbor)) {

                try {
                    Thread.sleep(500);  // slow propagation
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                MobileAgent clone = new MobileAgent(neighbor, base);
                new Thread(clone).start();
                System.out.println("Agent " + agentId + " cloned new agent at [" +
                        neighbor.getCol() + "," + neighbor.getRow() + "]");
            } else if (neighbor != null) {
                System.out.println("Agent " + agentId + " did not clone to [" +
                        neighbor.getCol() + "," + neighbor.getRow() + "] - Status: " + neighbor.getStatus());
            }
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void die() {
        isAlive = false;
    }
}
