package ch.bfh.ti.jts.simulation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.data.Net;

/**
 * Simulates traffic on a @{link ch.bfh.ti.jts.data.Net}
 *
 * @author ente
 */
public class Simulation {
    
    /**
     * Net for which to simulate traffic.
     */
    private final Net simulateNet;
    private long      lastTick;
    
    public Simulation(final Net simulateNet) {
        this.simulateNet = simulateNet;
        start();
    }
    
    public void start() {
        lastTick = System.nanoTime();
    }
    
    /**
     * Do a simulation step
     */
    public void tick() {
        final long now = System.nanoTime();
        // get diff time to last tick
        final long duration = now - lastTick;
        final double durationSeconds = duration * 1E-9;
        // build thread safe hash map which holds all the decisions of the
        // thinkables
        final Map<Thinkable, Decision> initDecisions = new HashMap<Thinkable, Decision>();
        simulateNet.getThinkableStream().sequential().forEach(e -> {
            initDecisions.put(e, new Decision());
        });
        final Map<Thinkable, Decision> decisions = Collections.unmodifiableMap(initDecisions);
        // think
        simulateNet.getThinkableStream().forEach(e -> {
            e.think(decisions.get(e));
        });
        // simulate
        simulateNet.getSimulatableStream().forEach(e -> {
            e.simulate(durationSeconds, decisions.get(e));
        });
        lastTick = now;
    }
}
