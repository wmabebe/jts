package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.util.concurrent.atomic.AtomicInteger;

import ch.bfh.ti.jts.gui.Renderable;

/**
 * Base class for all the elements
 * 
 * @author winki
 * @author ente
 */
public abstract class Element implements Renderable {
    
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
    private final int                  id      = NEXT_ID.incrementAndGet();
    
    /**
     * Gets the id of this element
     * 
     * @return the unique element id
     */
    public int getId() {
        return id;
    }
    
    /**
     * The layer of the object. 0: Background 1: First layer
     * 
     * @return the layer
     */
    public abstract int getLayer();
    
    @Override
    public abstract void render(final Graphics2D g);
}
