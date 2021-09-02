package ch.bfh.ti.jts.data;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import ch.bfh.ti.jts.exceptions.ArgumentNullException;

/**
 * Base class for all the elements.
 *
 * @author Enteee
 * @author winki
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
    private Net                        net;
    private boolean                    isRemoveCandidate;

    public Element(final String name) {
        this.name = name;
        id = NEXT_ID.incrementAndGet();
    }

    @Override
    public int compareTo(final Element otherElement) {
        return getId() - otherElement.getId();

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
     * Absolute distance to another element.
     *
     * @param element
     *            other element
     * @return distance in [m]
     */
    public double getDistance(final Element element) {
        return getDistance(element.getPosition());
    }

    /**
     * Absolute distance to the specified world coordinates.
     *
     * @param coordinates
     *            world coordinates
     * @return distance in [m]
     */
    public double getDistance(final Point2D coordinates) {
        return getPosition().distance(coordinates);
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

    public Net getNet() {
        if (net == null) {
            throw new ArgumentNullException("net");
        }
        return net;
    }

    /**
     * Absolute position of this element on the world.
     *
     * @return position as {@link Point2D}
     */
    public abstract Point2D getPosition();

    @Override
    public int hashCode() {
        return new Integer(id).hashCode();
    }

    /**
     * Is this element a remove candidate?
     *
     * @return @{code true} if yes, @{code false} otherwise.
     */
    public boolean isRemoveCandidate() {
        return isRemoveCandidate;
    }

    /**
     * Flag this element as remove candidate.
     */
    public void remove() {
        isRemoveCandidate = true;
    }

    public void setNet(final Net net) {
        if (net == null) {
            throw new ArgumentNullException("net");
        }
        this.net = net;
    }

    @Override
    public String toString() {
        return getName();
    }
}
