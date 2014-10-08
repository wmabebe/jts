package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.simulation.Simulatable;

/**
 * Data holder for a traffic net.
 * 
 * @author ente
 */
public class Net extends Element {
    
    public final static int                         NET_LAYER   = 0;
    private final Set<Element>                      elements    = new HashSet<Element>();
    private final Set<Simulatable>                  simulatable = new HashSet<Simulatable>();
    private final Set<Thinkable>                    thinkable   = new HashSet<Thinkable>();
    private final Map<Integer, Collection<Element>> layers      = new HashMap<Integer, Collection<Element>>(); ;
    
    public void addElement(final Element element) {
        elements.add(element);
        final int elementLayer = element.getLayer();
        // does the layer exist?
        if (!layers.containsKey(elementLayer)) {
            // add a new list container for elements
            layers.put(elementLayer, new LinkedList<Element>());
        }
        // add element to layer
        layers.get(elementLayer).add(element);
        // element thinkable?
        if (Thinkable.class.isInstance(element)) {
            thinkable.add((Thinkable) element);
        }
        // element simulatable?
        if (Simulatable.class.isInstance(element)) {
            simulatable.add((Simulatable) element);
        }
    }
    
    public Stream<Element> getElementStream() {
        return elements.stream().sequential();
    }
    
    public Stream<Thinkable> getThinkableStream() {
        return thinkable.stream().parallel();
    }
    
    public Stream<Simulatable> getSimulatableStream() {
        return simulatable.stream().sequential();
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
