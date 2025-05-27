import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Separate class to clean up the Sensor object of all the Sensors connected
 * through the inner class Node's pointers
 * Makes it easy for the Java Garbage Collector to do its thing
 * Reducing potential for memory leaks
 */
public class MarkForNegation {
    /**
     * Clean-up everything
     * Setting all values to Null such as the pointers etc
     */
    public synchronized static void markSensorsForDeletion(Sensor s,
                                                 Set<Sensor> visited) {
        // Double check to see if we've already deleted the current Sensor
        // being traversed
        if (s == null || visited.contains(s)) {
            return;
        }
        visited.add(s);

        // Use recursion to traverse and remove all values for ptrs
        for (Sensor.Node node :
                Collections.synchronizedSet(new HashSet<>(s.node.getPtrs()))) {
            s.node.getPtrs().remove(node);
            node.getPtrs().remove(s.node);
            markSensorsForDeletion(node.getOwner(), visited);
        }
        // Finish by marking the current Sensor
        s.node.getPtrs().clear();
    }
}