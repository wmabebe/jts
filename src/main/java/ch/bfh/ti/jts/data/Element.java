package ch.bfh.ti.jts.data;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for all the elements.
 *
 * @author winki
 * @author ente
 */
public abstract class Element implements Serializable, Comparable<Element> {
    
    /**
     * Represents an element at a specific time. Natural order is descending
     * time (most recent first).
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
        
        @Override
        public int compareTo(final ElementInTime o) {
            return new Double(o.getTime()).compareTo(getTime());
        }
        
        public Element getElement() {
            return element;
        }
        
        public double getTime() {
            return time;
        }
        
    }
    
    private static final long          serialVersionUID = 1L;
    private static final AtomicInteger NEXT_ID          = new AtomicInteger(0);
    
    private final String               name;
    private final int                  id;
    private Net net;
    
    public Element(final String name) {
        this.name = name;
        id = NEXT_ID.incrementAndGet();
    }    
    
    public void setNet(Net net) {
        this.net = net;
    }    
    
    public Net getNet() {
        return net;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Element)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        final Element elem = (Element) obj;
        return new Integer(elem.id).equals(id);
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
     * Gets the name of this element (from xml source files).
     *
     * @return the xml name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Absolute position of this element on the world.
     * 
     * @return position as x and y coordinates
     */
    public abstract Point2D getPosition();
    
    /**
     * Absolute distance to another element.
     * 
     * @param element
     *            other element
     * @return distance in [m]
     */
    public double getDistance(Element element) {
        return getDistance(element.getPosition());
    }
    
    /**
     * Absolute distance to the specified world coordinates.
     * 
     * @param coordinates
     *            world coordinates
     * @return distance in [m]
     */
    public double getDistance(Point2D coordinates) {
        return getPosition().distance(coordinates);
    }
    
    @Override
    public int hashCode() {
        return new Integer(id).hashCode();
    }
    
    @Override
    public int compareTo(final Element otherElement) {
        return getId() - otherElement.getId();
        
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
