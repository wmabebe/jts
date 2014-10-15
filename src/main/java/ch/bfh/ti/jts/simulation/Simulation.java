package ch.bfh.ti.jts.simulation;

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
        // think
        simulateNet.think();
        // simulate
        simulateNet.simulate(durationSeconds);
        // set lastTick for timediff
        lastTick = now;
    }
}
