import java.util.*;
import java.io.*;

public class BaseStation {
    private final Map<String, Sensor> sensors = new HashMap<>();
    private final List<String> agentLog = new ArrayList<>();
    private Sensor baseSensor;
    private Sensor fireStart;

    private String coordKey(int x, int y) {
        return x + "," + y;
    }

    public void startSimulation() {
        if (baseSensor != null) {
            MobileAgent agent = new MobileAgent(baseSensor, this);
            new Thread(agent).start();
        }
    }

    public synchronized void logAgent(String id, Sensor sensor) {
        agentLog.add("Agent " + id + " created at [" + sensor.getCol() + "," + sensor.getRow() + "]");
    }

    public List<String> getAgentLog() {
        return agentLog;
    }
}
