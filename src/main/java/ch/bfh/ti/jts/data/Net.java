package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.Helpers;
import ch.bfh.ti.jts.utils.layers.Layers;

/**
 * Data holder for a traffic net.
 *
 * @author ente
 */
public class Net implements Serializable {
    
    private static final long          serialVersionUID = 1L;
    public final static int            NET_RENDER_LAYER = 0;
    private final Set<Element>         elements         = new HashSet<Element>();
    private final Layers<Element>      renderables      = new Layers<>();
    private final Set<Thinkable>       thinkables       = new HashSet<Thinkable>();
    private final Layers<Simulatable>  simulatables     = new Layers<Simulatable>();
    private final BlockingQueue<Route> routes           = new LinkedBlockingQueue<Route>();
    
    public void addElement(final Element element) {
        elements.add(element);
        renderables.addLayerable(element.getRenderLayer(), element);
        // element thinkable?
        if (Thinkable.class.isInstance(element)) {
            final Thinkable thinkable = (Thinkable) element;
            thinkables.add(thinkable);
        }
        // element simulatable?
        if (Simulatable.class.isInstance(element)) {
            final Simulatable simulatable = (Simulatable) element;
            simulatables.addLayerable(simulatable.getSimulationLayer(), simulatable);
        }
    }
    
    public void addAgent(final Agent agent, final Route route) {
        final Lane lane = route.getRouteStart().getFirstLane();
        agent.setLane(lane);
        agent.setVehicle(route.getVehicle());
        final double relativePositionOnLane = route.getDeparturePos();
        agent.setRelativePosition(Helpers.clamp(relativePositionOnLane, 0.0, 1.0));
        agent.setVelocity(route.getDepartureSpeed());
        addElement(agent);
    }
    
    public void addRoutes(final Collection<Route> routes) {
        this.routes.addAll(routes);
    }
    
    public Collection<Route> getRoutes() {
        return routes;
    }
    
    public Stream<Element> getElementStream() {
        return elements.stream().sequential();
    }
    
    public Stream<Element> getElementStream(Class<?> filter) {
        return elements.stream().sequential().filter(x -> x.getClass() == filter);
    }
    
    public void think() {
        thinkables.stream().parallel().forEach(e -> {
            e.think();
        });
    }
    
    public void simulate(double duration) {
        for (int layer : simulatables.getLayersIterator()) {
            simulatables.getLayerStream(layer).sequential().forEach(e -> {
                e.simulate(duration);
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
