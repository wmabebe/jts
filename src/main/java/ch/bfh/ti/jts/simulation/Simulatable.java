package ch.bfh.ti.jts.simulation;

import ch.bfh.ti.jts.ai.Decision;
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
     * @param decision
     *            Decision made by this {@link Simulatable}, {@code null} if the
     *            {@link Simulatable} does not implement the thinkable interface
     */
    void simulate(final double duration, final Decision decision);
}
