package ch.bfh.ti.jts.simulation;

import ch.bfh.ti.jts.data.Element;

/**
 * Interface implemented by {@link Element} which can be simulated.
 *
 * @author ente
 */
public interface Simulatable {
    
    /**
     * The simulation layer of the object. 0: Simulate first 1: Simulate second
     *
     * @return the layer
     */
    int getSimulationLayer();
    
    /**
     * Called in each simulation step
     *
     * @param duration
     *            duration to simulate in seconds
     */
    void simulate(final double duration);
}
