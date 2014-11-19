package ch.bfh.ti.jts.gui;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Net;

public interface Renderable {
    
    /**
     * Known classes to layer mappings
     */
    static Map<Class<?>, Integer> KNOWN_CLASSES = new HashMap<Class<?>, Integer>() {
                                                    
                                                    private static final long serialVersionUID = 1L;
                                                    
                                                    {
                                                        put(Agent.class, 3);
                                                        put(Lane.class, 2);
                                                        put(Edge.class, 1);
                                                        put(Junction.class, 1);
                                                        put(Net.class, 0);
                                                    }
                                                };
    
    /**
     * The rendering layer of the object. 0: Background 1: First layer
     *
     * @return the layer
     */
    default int getRenderLayer() {
        if (!KNOWN_CLASSES.containsKey(this.getClass())) {
            throw new AssertionError("invalid layer", new IndexOutOfBoundsException(this.getClass() + " is not a known class"));
        }
        return KNOWN_CLASSES.get(getClass());
    }
    
    /**
     * Render the implementing object.
     *
     * @param g
     *            the object to render with.
     */
    void render(Graphics2D g);
}
