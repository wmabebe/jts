package ch.bfh.ti.jts.data;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for all the elements
 *
 * @author winki
 * @author ente
 */
public abstract class Element implements Serializable {
    
    /**
     * Represents an element at a specific time;
     * 
     * @author ente
     */
    public static class ElementInTime implements Comparable<ElementInTime> {
        
        private final double  time;
        private final Element element;
        
        public ElementInTime(final double time, final Element element) {
            this.time = time;
            this.element = element;
        }
        
        public double getTime() {
            return time;
        }
        
        public Element getElement() {
            return element;
        }
        
        @Override
        public int compareTo(ElementInTime o) {
            return (new Double(time)).compareTo(o.getTime());
        }
        
    }
    
    private static final long          serialVersionUID = 1L;
    private final String               name;
    private static final AtomicInteger NEXT_ID          = new AtomicInteger(0);
    private final int                  id               = NEXT_ID.incrementAndGet();
    
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
    
    @Override
    public String toString() {
        return getName();
    }
}
