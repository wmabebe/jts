package ch.bfh.ti.jts.data;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.ai.agents.IdleAgent;
import ch.bfh.ti.jts.exceptions.ArgumentNullException;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.Config;
import ch.bfh.ti.jts.utils.Helpers;
import ch.bfh.ti.jts.utils.graph.GPS;
import ch.bfh.ti.jts.utils.layers.Layers;

/**
 * Data holder for a traffic net.
 *
 * @author Enteee
 * @author winki
 */
public class Net extends Element implements Serializable, Simulatable {
    
    private static final long              serialVersionUID  = 1L;
    private static final Logger            log               = LogManager.getLogger(Net.class);
    
    /**
     * Factor by which the spawning should take place. 1 means real time speed.
     * 1440 = 1 day in one minute
     */
    private final double                   SPAWN_TIME_FACTOR = Config.getInstance().getDouble("net.spawning.timefactor", 1440.0, 0.0000000001, 10000000000.0);
    private final Set<Element>             elements          = new HashSet<>();
    private final Layers<Renderable>       renderables       = new Layers<>();
    private final Set<Thinkable>           thinkables        = new HashSet<>();
    private final Layers<Simulatable>      simulatables      = new Layers<>();
    private final BlockingQueue<SpawnInfo> routes            = new LinkedBlockingQueue<>();
    /**
     * Absolute time at simulated [s].
     */
    private double                         simulationTime;
    
    public Net() {
        super("Net");
        addElement(this);
    }
    
    public void addElement(final Element element) {
        // add element to net
        elements.add(element);
        // set net on element
        element.setNet(this);
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
    
    public void addRoutes(final Collection<SpawnInfo> routes) {
        this.routes.addAll(routes);
    }
    
    private Agent createAgent(String name) {
        if (name != null) {
            if (!name.endsWith("Agent")) {
                // add suffix
                name = name.concat("Agent");
            }
            try {
                // append namespace
                name = String.format("ch.bfh.ti.jts.ai.agents.%s", name);
                final Class<?> clazz = Class.forName(name);
                final Constructor<?> ctor = clazz.getConstructor();
                final Object object = ctor.newInstance();
                log.debug("Create agent: " + object.getClass());
                
                return (Agent) object;
            } catch (final Exception e) {
                log.warn("Creating agent failed: " + name);
            }
        }
        // default agent
        log.debug("Create default agent: " + IdleAgent.class);
        return new IdleAgent();
    }
    
    private void doSpawning() {
        
        final List<Route> routes = getRoutes().stream().sequential().filter(x -> x instanceof Route).filter(x -> x.getDepartureTime() < getSimulationTime() * SPAWN_TIME_FACTOR).map(x -> (Route) x)
                .collect(Collectors.toList());
        for (final Route route : routes) {
            
            // create agent
            final Agent agent = createAgent(route.getVehicle().getAgent());
            spawn(route, agent);
            
            // remove route (one one spawn per route)
            getRoutes().remove(route);
        }
        
        final List<Flow> flows = getRoutes().stream().sequential().filter(x -> x instanceof Flow).filter(x -> x.getDepartureTime() < getSimulationTime() * SPAWN_TIME_FACTOR).map(x -> (Flow) x)
                .collect(Collectors.toList());
        for (final Flow flow : flows) {
            if (flow.isSpawn(getSimulationTime())) {
                
                // create agent
                final Agent agent = createAgent(flow.getVehicle().getAgent());
                spawn(flow, agent);
                
                // don't remove flow (infinite spawning)
            }
        }
    }
    
    public boolean removeAgent(int id) {
        Element element = getElement(id);
        if (element != null) {
            if (element instanceof Agent) {
                final Agent agent = (Agent) element;
                agent.remove();
                return true;
            }
        }
        return false;
    }
    
    private void doDespawning() {
        // remove agents...
        elements.removeIf(x -> {
            if (x != null && x instanceof Agent) {
                final Agent a = (Agent) x;
                return a.isRemoveCandidate();
            }
            return false;
        });
        thinkables.removeIf(x -> {
            if (x != null && x instanceof Agent) {
                final Agent a = (Agent) x;
                return a.isRemoveCandidate();
            }
            return false;
        });
        renderables.removeAgents();
        simulatables.removeAgents();
    }
    
    public GPS<Junction, Edge> getGPS() {
        return new GPS<Junction, Edge>(this);
    }
    
    public Element getElement(final int elementId) {
        return elements.stream().filter(x -> x.getId() == elementId).findAny().orElse(null);
    }
    
    public Stream<Element> getElementStream() {
        return elements.stream().sequential();
    }
    
    public Stream<Element> getElementStream(final Class<?> filter) {
        return elements.stream().sequential().filter(x -> filter.isAssignableFrom(x.getClass()));
    }
    
    public Stream<Element> getElementStream(final Collection<Class<?>> filter) {
        return elements.stream().sequential().filter(x -> filter.stream().anyMatch(f -> f.isAssignableFrom(x.getClass())));
    }
    
    public Layers<Renderable> getRenderable() {
        return renderables;
    }
    
    public Collection<SpawnInfo> getRoutes() {
        return routes;
    }
    
    public Layers<Simulatable> getSimulatable() {
        return simulatables;
    }
    
    public Stream<Thinkable> getThinkableStream() {
        return thinkables.stream().parallel();
    }
    
    /**
     * Get the total simulated time in [s]
     *
     * @return total simulated time in [s]
     */
    public double getSimulationTime() {
        return simulationTime;
    }
    
    @Override
    public void simulate(final double duration) {
        simulationTime += duration;
        doSpawning();
        doDespawning();
    }
    
    private void spawn(final SpawnInfo spawnInfo, final Agent agent) {
        assert agent != null;
        
        final SpawnLocation spawnLocation = spawnInfo.getStart();
        assert spawnLocation != null;
        
        final Lane lane = spawnLocation.getSpawnLane();
        if (lane == null) {
            throw new ArgumentNullException("lane");
        }
        
        final double posOnLane = Helpers.clamp(spawnInfo.getDeparturePos(), 0.0, lane.getLength());
        agent.init(posOnLane, spawnInfo.getVehicle(), spawnInfo.getDepartureSpeed(), spawnInfo);
        
        addElement(agent);
        
        agent.setLane(lane);
        lane.addLaneAgent(agent);
        log.debug(agent + " spawned at: " + lane);
    }
    
    /**
     * Returns the element that is nearest to a specified coordinat pair. The
     * elements can be filtered by max distance away from the coordinates and by
     * its type.
     * 
     * @param maxDistance
     *            max distance that the element can be away from the coordinates
     *            [m].
     * @param types
     *            types of the elements that should be considered
     * @return nearest element
     */
    public Element getElementByCoordinates(Point2D coordinates, double maxDistance, final Collection<Class<?>> types) {
        Element nearestElement = null;
        double minDistance = Double.MAX_VALUE;
        List<Element> elements = getElementStream(types).filter(x -> x.getDistance(coordinates) <= maxDistance).collect(Collectors.toList());
        for (Element element : elements) {
            if (element.getDistance(coordinates) < minDistance) {
                nearestElement = element;
                minDistance = element.getDistance(coordinates);
            }
        }
        return nearestElement;
    }
    
    @Override
    public Point2D getPosition() {
        return new Point2D.Double(); // not supported yet
    }
}
