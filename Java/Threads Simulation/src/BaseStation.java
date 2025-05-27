import java.util.*;

public class BaseStation extends Observable implements Observer, Runnable {
    private final Map<String, Sensor> sensors;
    private final List<String> agentLog;
    private final Sensor baseSensor;
    // Increment to this whenever creating a new agent
    private int agentNumber;

    /**
     * Constructor for baseSensor
     * @param baseSensor the location of the baseSensor
     */
    public BaseStation (Sensor baseSensor) {
        this.baseSensor = baseSensor;
        sensors = Collections.synchronizedMap(new HashMap<>());
        agentLog = Collections.synchronizedList(new ArrayList<>());
        agentNumber = 0;
    }
    public synchronized void logAgent(MobileAgent agent) {
        agent.addObserver(this);
    }

    /**
     * Getter for agent log
     * @return the agent log for this BaseStation
     */
    public synchronized List<String> getAgentLog() {
        return agentLog;
    }
    /**
     * Explore the neighbors of the base station and create a new mobile
     * agent in every neighboring sensor
     */
    public void sendOutAgents () {
        for (Sensor.Node n : baseSensor.node.getPtrs()) {
            MobileAgent agent = new MobileAgent(n.getOwner(), this);
            agent.addObserver(this);
            new Thread(agent).start();
        }
    }
    public synchronized void addToLog (String msg) {
        agentLog.add(msg);
        setChanged();
        notifyObservers(msg);
    }
    public int generateId () {
        return agentNumber++;
    }
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String message = (String) arg;
            String[] args = message.split("\\s+");
            switch (args[0]) {
                case StationMessages.DISPATCH:
                    // args[1] should be the agent's ID
                    // args[2] should be the row it was dispatched to
                    // args[3] should be the col it was dispatched to
                    addToLog("Agent " + args[1] + "was dispatched" +
                            args[2] + "," + args[3]);
                    break;
                case StationMessages.BURN:
                    // args[1] should be the row of the agent
                    // args[2] should be the col of the agent
                    addToLog("Fire found at : " + args[1] + ", " + args[2]);
                    break;
                case StationMessages.DEATH:
                    // args[1] should be the ID
                    addToLog("Agent " + args[1] + " has died.");
                    break;
            }
        }
    }
    @Override
    public void run() {
        if(!baseSensor.getStatus().equals(SensorStatus.BURNING)) {
            sendOutAgents();
        }
    }
}
