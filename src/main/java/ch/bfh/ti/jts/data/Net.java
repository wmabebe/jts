package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.layers.Layers;

/**
 * Data holder for a traffic net.
 *
 * @author ente
 */
public class Net {
    
    public final static int           NET_RENDER_LAYER = 0;
    private final Set<Element>        elements         = new HashSet<Element>();
    private final Layers<Element>     renderables      = new Layers<>();
    private final Set<Thinkable>      thinkables       = new HashSet<Thinkable>();
    private final Layers<Simulatable> simulatables     = new Layers<Simulatable>();
    final Map<Thinkable, Decision>    decisions        = new HashMap<Thinkable, Decision>();
    
    public void addElement(final Element element) {
        elements.add(element);
        renderables.addLayerable(element.getRenderLayer(), element);
        if (Thinkable.class.isInstance(element)) {
            final Thinkable thinkable = (Thinkable) element;
            thinkables.add(thinkable);
            decisions.put(thinkable, new Decision());
        }
        // element simulatable?
        if (Simulatable.class.isInstance(element)) {
            final Simulatable simulatable = (Simulatable) element;
            simulatables.addLayerable(simulatable.getSimulationLayer(), simulatable);
        }
    }
    
    public Stream<Element> getElementStream() {
        return elements.stream().sequential();
    }
    
    public void think() {
        thinkables.stream().parallel().forEach(e -> {
            e.think(decisions.get(e));
        });
    }
    
    public void simulate(double duration) {
        for (int layer : simulatables.getLayersIterator()) {
            simulatables.getLayerStream(layer).sequential().forEach(e -> {
                e.simulate(duration, decisions.get(e));
            });
        }
    }
    
    public void render(final Graphics2D g) {
        for (int layer : renderables.getLayersIterator()) {
            renderables.getLayerStream(layer).sequential().forEach(e -> {
                e.render(g);
            });
        }
    }
}
