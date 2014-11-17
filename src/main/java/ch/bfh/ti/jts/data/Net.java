package ch.bfh.ti.jts.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
     * Absolute time at which the simulation started [s].
     */
    private final double               startTime;
    /**
     * Absolute time at which the the latest simulation tick took place [s].
     */
    private double                     lastTick;
    
    public Net() {
        super("Net");
        startTime = System.nanoTime() * 1E-9;
        lastTick = startTime;
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
    
    public Element getElement(final int elementId) {
        return elements.stream().filter(x -> x.getId() == elementId).findAny().orElse(null);
    }
    
    public Stream<Element> getElementStream() {
        return elements.stream().sequential();
    }
    
    public Stream<Element> getElementStream(final Class<?> filter) {
        return elements.stream().sequential().filter(x -> x.getClass() == filter);
    }
    
    public Layers<Renderable> getRenderable() {
        return renderables;
    }
    
    public Collection<Route> getRoutes() {
        return routes;
    }
    
    public Layers<Simulatable> getSimulatable() {
        return simulatables;
    }
    
    @Override
    public int getSimulationLayer() {
        return NET_SIMULATION_LAYER;
    }
    
    public Stream<Thinkable> getThinkableStream() {
        return thinkables.stream().parallel();
    }
    
    /**
     * Get the total ticked time in [s]
     * 
     * @return total ticked time in [s]
     */
    public double getTimeTotal() {
        return lastTick - startTime;
    }
    
    /**
     * Do tick time.
     * 
     * @return delta time since last tick in [s]
     */
    public double tick() {
        double now = System.nanoTime() * 1E-9;
        double tickDelta = now - lastTick;
        lastTick = now;
        return tickDelta;
    }
    
    @Override
    public void simulate(final double duration) {
        spawn();
    }
    
    private void spawn() {
        
        final List<Route> routes = getRoutes().stream().sequential().filter(x -> x.getDepartureTime() < getTimeTotal() * SPAWN_TIME_FACTOR).collect(Collectors.toList());
        for (final Route route : routes) {
            final Agent agent = createAgent();
            final Lane lane = route.getRouteStart().getFirstLane();
            agent.setLane(lane);
            agent.setVehicle(route.getVehicle());
            final double relativePositionOnLane = route.getDeparturePos() / lane.getLength();
            agent.setRelativePosition(Helpers.clamp(relativePositionOnLane, 0.0, 1.0));
            agent.setVelocity(route.getDepartureSpeed());
            addElement(agent);
            lane.addAgent(agent);
            getRoutes().remove(route);
            Logger.getLogger(Net.class.getName()).info("Agent spawned at: " + lane + " pos:" + relativePositionOnLane);
            
        }
    }
    
    // TODO: create different types of agents here
    private Agent createAgent() {
        // return new RealisticAgent();
        return new FullSpeedAgent();
    }
}
