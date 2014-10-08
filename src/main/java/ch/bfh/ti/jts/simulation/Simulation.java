package ch.bfh.ti.jts.simulation;

import java.time.Duration;
import java.time.Instant;
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
    private Instant   lastTick;
    
    public Simulation(final Net simulateNet) {
        this.simulateNet = simulateNet;
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
            e.simulate(duration, decisions.get(e));
        });
        lastTick = now;
    }
}
