package ch.bfh.ti.jts.data;

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
    
    private static final long              serialVersionUID  = 1L;
    public final static Logger             LOG               = LogManager.getLogger(Net.class);
    /**
     * Factor by which the spawning should take place. 1 means real time speed.
     * 1440 = 1 day in one minute
     */
    private final double                   SPAWN_TIME_FACTOR = 1440;
    private final Set<Element>             elements          = new HashSet<>();
    private final Layers<Renderable>       renderables       = new Layers<>();
    private final Set<Thinkable>           thinkables        = new HashSet<>();
    private final Layers<Simulatable>      simulatables      = new Layers<>();
    private final BlockingQueue<SpawnInfo> routes            = new LinkedBlockingQueue<>();
    /**
     * Absolute time at simulated [s].
     */
    private double                         simulationTime    = 0;
    
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
                LOG.debug("Create agent: " + object.getClass());
                
                return (Agent) object;
            } catch (final Exception e) {
                LOG.warn("Creating agent failed: " + name);
            }
        }
        // default agent
        LOG.debug("Create default agent: " + IdleAgent.class);
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
    
    private void removeDespawnedAgents() {
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
    
    @Override
    public void simulate(final double duration) {
        simulationTime += duration;
        doSpawning();
        removeDespawnedAgents();
    }
    
    private void spawn(final SpawnInfo spawnInfo, final Agent agent) {
        assert agent != null;
        
        final SpawnLocation spawnLocation = spawnInfo.getStart();
        assert spawnLocation != null;
        
        final Lane lane = spawnLocation.getSpawnLane();
        if (lane == null) {
            throw new RuntimeException("lane is null");
        }
        
        final double posOnLane = Helpers.clamp(spawnInfo.getDeparturePos(), 0.0, lane.getLength());
        agent.init(posOnLane, spawnInfo.getVehicle(), spawnInfo.getDepartureSpeed(), spawnInfo);
        
        addElement(agent);
        
        agent.setLane(lane);
        lane.addLaneAgent(agent);
        LOG.debug(agent + " spawned at: " + lane);
    }
    
}
