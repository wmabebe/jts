package ch.bfh.ti.jts.simulation;

import java.util.HashMap;
import java.util.Map;

import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Element;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Net;

/**
 * Interface implemented by {@link Element} which can be simulated.
 *
 * @author Enteee
 * @author winki
 */
public interface Simulatable {
    
    /**
     * Known classes to layer mappings
     */
    static Map<Class<?>, Integer> KNOWN_CLASSES = new HashMap<Class<?>, Integer>() {
                                                    
                                                    private static final long serialVersionUID = 1L;
                                                    
                                                    {
                                                        put(Agent.class, 0);
                                                        put(Lane.class, 1);
                                                        put(Edge.class, 2);
                                                        put(Junction.class, 3);
                                                        put(Net.class, 4);
                                                    }
                                                };
    
    /**
     * The simulation layer of the object. 0: Simulate first 1: Simulate second
     *
     * @return the layer
     */
    default int getSimulationLayer() {
        if (!KNOWN_CLASSES.containsKey(getClass())) {
            throw new AssertionError("invalid layer", new IndexOutOfBoundsException(getClass() + " is not a known class"));
        }
        return KNOWN_CLASSES.get(getClass());
    }
    
    /**
     * Called in each simulation step
     *
     * @param duration
     *            duration to simulate in seconds
     */
    void simulate(final double duration);
}
