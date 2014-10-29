package ch.bfh.ti.jts.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.ai.agents.FullSpeedAgent;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.Helpers;
import ch.bfh.ti.jts.utils.layers.Layers;

/**
 * Data holder for a traffic net.
 *
 * @author ente
 */
public class Net extends Element implements Serializable, Simulatable {
    
    private static final long          serialVersionUID     = 1L;
    public final static int            NET_RENDER_LAYER     = 0;
    public final static int            NET_SIMULATION_LAYER = Junction.JUNCTION_SIMULATION_LAYER + 1;
    /**
     * Factor by which the spawning should take place. 1 means real time speed.
     * 1440 = 1 day in one minute
     */
    private final double               SPAWN_TIME_FACTOR    = 1440;
    private final Set<Element>         elements             = new HashSet<>();
    private final Layers<Renderable>   renderables          = new Layers<>();
    private final Set<Thinkable>       thinkables           = new HashSet<>();
    private final Layers<Simulatable>  simulatables         = new Layers<>();
    private final BlockingQueue<Route> routes               = new LinkedBlockingQueue<>();
    /**
     * Total time that has passed since the begin of the simulation [s].
     */
    private double                     timeTotal;
    
    public Net() {
        super("Net");
        addElement(this);
    }
    
    public void addElement(final Element element) {
        elements.add(element);
        // element renderable?
        if (Renderable.class.isInstance(element)) {
            final Renderable renderable = (Renderable) element;
            renderables.addLayerable(renderable.getRenderLayer(), renderable);
        }
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
    
    public Stream<Thinkable> getThinkableStream() {
        return thinkables.stream().parallel();
    }
    
    public Layers<Simulatable> getSimulatable() {
        return simulatables;
    }
    
    public Layers<Renderable> getRenderable() {
        return renderables;
    }
    
    public double getTimeTotal() {
        return timeTotal;
    }
    
    @Override
    public int getSimulationLayer() {
        return NET_SIMULATION_LAYER;
    }
    
    public void simulate(double duration) {
        timeTotal += duration;
        // agent spawning
        List<Route> routes = getRoutes().stream().sequential().filter(x -> x.getDepartureTime() < timeTotal * SPAWN_TIME_FACTOR).collect(Collectors.toList());
        for (Route route : routes) {
            final Agent agent = new FullSpeedAgent();
            final Lane lane = route.getRouteStart().getFirstLane();
            lane.getAgents().add(agent);
            agent.setLane(lane);
            agent.setVehicle(route.getVehicle());
            final double relativePositionOnLane = route.getDeparturePos();
            agent.setRelativePosition(Helpers.clamp(relativePositionOnLane, 0.0, 1.0));
            agent.setVelocity(route.getDepartureSpeed());
            addElement(agent);
            getRoutes().remove(route);
            Logger.getGlobal().log(Level.INFO, "agent spawned");
        }
    }
    
}
