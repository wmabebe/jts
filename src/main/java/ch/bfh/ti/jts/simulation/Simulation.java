package ch.bfh.ti.jts.simulation;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.data.Element;
import ch.bfh.ti.jts.utils.deepcopy.DeepCopy;

/**
 * Simulates traffic on a @{link ch.bfh.ti.jts.data.Net}
 * 
 * @author ente
 */
public class Simulation<T extends Element> {
    
    /**
     * Maximum number of old simulatables to keep
     */
    private final static int OLD_SIMULATABLES_KEEP = 100;
    /**
     * Net for which to simulate traffic.
     */
    private final List<T>    oldSimulatables       = new LinkedList<T>();
    private final T          simulatable;
    private Instant          lastTick;
    
    public Simulation(final T simulatable) {
        this.simulatable = simulatable;
        start();
    }
    
    public void start() {
        lastTick = Instant.now();
    }
    
    /**
     * Do a simulation step
     */
    public void tick() {
       
        final Instant now = Instant.now();
        // get diff to last tick
        final Duration duration = Duration.between(lastTick, now);
        // serialize
        /*
         * final T oldSimulatable = (T) DeepCopy.copy(simulatable);
         * oldSimulatables.add(oldSimulatable); if (oldSimulatables.size() >
         * OLD_SIMULATABLES_KEEP) { oldSimulatables.remove(0); }
         */
        simulatable.simulate(duration);
        lastTick = now;
    }
}
