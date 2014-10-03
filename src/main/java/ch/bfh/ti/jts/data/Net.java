package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import ch.bfh.ti.jts.gui.Renderable;

/**
 * Data holder for a traffic net.
 * 
 * @author ente
 */
public class Net extends Element implements Renderable {
    
    public final static int                         NET_LAYER = 0;
    private final Map<Integer, Element>             elements  = new HashMap<Integer, Element>();
    private final Map<Integer, Collection<Element>> layers    = new HashMap<Integer, Collection<Element>>(); ;
    
    /**
     * Get the copy of the given element in this net.
     * 
     * @param element
     *            the element looked up
     * @return the element of this net, {@code null} if the element was not
     *         found
     */
    public Element getElement(final Element element) {
        return elements.get(element.getId());
    }
    
    public void addElement(final Element element) {
        elements.put(element.getId(), element);
        final int elementLayer = element.getLayer();
        // does the layer exist?
        if (!layers.containsKey(elementLayer)) {
            // add a new list container for elements
            layers.put(elementLayer, new LinkedList<Element>());
        }
        // add element to layer
        layers.get(elementLayer).add(element);
    }
    
    public Collection<Element> getElements() {
        return elements.values();
    }
    
    @Override
    public int getLayer() {
        return NET_LAYER;
    }
    
    @Override
    public void render(final Graphics2D g) {
        // get a sorted list of all layers
        final TreeSet<Integer> sortedLayers = new TreeSet<Integer>(layers.keySet());
        // render the layers in order
        for (final Integer layer : sortedLayers) {
            for (final Element element : layers.get(layer)) {
                element.render(g);
            }
        }
    }
}
