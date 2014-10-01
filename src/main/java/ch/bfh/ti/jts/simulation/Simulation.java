package ch.bfh.ti.jts.simulation;

import ch.bfh.ti.jts.data.Agent;
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
    private final Net net;
    
    public Simulation(final Net net) {
        this.net = net;
    }
    
    /**
     * Do a simulation step
     */
    public void tick() {
        // first simulate all agents
        for (final Agent agent : net.getAgents()) {
        }
    }
}
