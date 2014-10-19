package ch.bfh.ti.jts.data;

import java.util.concurrent.atomic.AtomicInteger;

import ch.bfh.ti.jts.gui.Renderable;

/**
 * Base class for all the elements
 *
 * @author winki
 * @author ente
 */
public abstract class Element implements Renderable {
    
    private String                     name;
    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
    private final int                  id      = NEXT_ID.incrementAndGet();
    
    public Element(final String name) {
        this.name = name;
    }
    
    /**
     * Gets the id of this element
     *
     * @return the unique element id
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the id of this element (from xml source files)
     * 
     * @return the xml name
     */
    public String getName() {
        return name;
    }
}
