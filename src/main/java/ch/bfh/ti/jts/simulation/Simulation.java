package ch.bfh.ti.jts.simulation;

import java.util.LinkedList;
import java.util.List;

import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.utils.deepcopy.DeepCopy;

/**
 * Simulates traffic on a @{link ch.bfh.ti.jts.data.Net}
 * 
 * @author ente
 */
public class Simulation {
    
    /**
     * Net for which to simulate traffic.
     */
    private final List<Net> nets = new LinkedList<Net>();
    
    public Simulation(final Net net) {
        this.nets.add(net);
    }
    
    /**
     * Do a simulation step
     */
    public void tick() {
        // serialize
        final Net oldNet = nets.get(0);
        final Net newNet = (Net) DeepCopy.copy(oldNet);
        // simulation step for each element
        newNet.getElements().stream().parallel().forEach(e -> {
            oldNet.getElement(e);
        });
    }
}
