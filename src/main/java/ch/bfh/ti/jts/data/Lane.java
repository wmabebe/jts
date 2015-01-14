package ch.bfh.ti.jts.data;

import static ch.bfh.ti.jts.utils.Helpers.getHeatColor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bfh.ti.jts.App;
import ch.bfh.ti.jts.exceptions.ArgumentNullException;
import ch.bfh.ti.jts.gui.PolyShape;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.simulation.Statistics;
import ch.bfh.ti.jts.utils.Config;

/**
 * Lanes are the container within agents can move.
 *
 * @author Enteee
 * @author winki
 */
public class Lane extends Element implements SpawnLocation, Simulatable, Renderable {
    
    private static final long                      serialVersionUID  = 1L;
    private static final Logger                    log               = LogManager.getLogger(Lane.class);
    public final static boolean                    LANE_RENDER_INFOS = Config.getInstance().getBool("lane.render.infos", false);
    
    private final Edge                             edge;
    private final int                              index;
    private final double                           speed;
    private final double                           length;
    private final PolyShape                        polyShape;
    /**
     * Lanes which are connected to this lane (over a junction)
     */
    private final Collection<Lane>                 lanes;
    /**
     * Agents on line. Key: RelativePosition, Value: List of @{link Agent}s
     */
    private final NavigableMap<Double, Set<Agent>> laneAgents;
    
    /**
     * Agents which have reached the end of the lane.
     */
    final Set<Agent>                               edgeLeaveCandidates;
    
    private double                                 spaceMeanSpeed;
    private double                                 timeMeanSpeed;
    private double                                 density;
    
    public Lane(final String name, final Edge edge, final int index, final double speed, final double length, final PolyShape polyShape) {
        super(name);
        if (edge == null) {
            throw new ArgumentNullException("edge");
        }
        if (polyShape == null) {
            throw new ArgumentNullException("polyShape");
        }
        this.edge = edge;
        this.edge.addLane(this);
        this.index = index;
        this.speed = speed;
        this.length = length;
        this.polyShape = polyShape;
        lanes = new LinkedList<>();
        laneAgents = new TreeMap<>();
        edgeLeaveCandidates = new HashSet<>();
    }
    
    public void addEdgeLeaveCandidate(final Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent");
        }
        edgeLeaveCandidates.add(agent);
    }
    
    /**
     * Add a agent to the list of agents on this list.
     *
     * @param agent
     *            the agent to add
     */
    public void addLaneAgent(final Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent");
        }
        Set<Agent> agentsAtPosition = laneAgents.get(agent.getRelativeLanePosition());
        if (agentsAtPosition == null) {
            // position not yet known.
            agentsAtPosition = new HashSet<>();
            laneAgents.put(agent.getRelativeLanePosition(), agentsAtPosition);
        }
        agentsAtPosition.add(agent);
    }
    
    public boolean comesFrom(final Junction junction) {
        return getEdge().getStart() == junction;
    }
    
    /**
     * Gets a flat collection of all agents on this lane in ascending order.
     *
     * @return all agents on the lane
     */
    public Collection<Agent> getAgentsInOrder() {
        final Collection<Agent> list = new LinkedList<>();
        for (final Set<Agent> agents : laneAgents.values()) {
            for (final Agent agent : agents) {
                list.add(agent);
            }
        }
        return list;
    }
    
    public Edge getEdge() {
        return edge;
    }
    
    @Override
    public Point2D getPosition() {
        Point2D position = getEdge().getPosition();
        return new Point2D.Double(position.getX(), position.getY() - 15 - 5 * getIndex());
    }
    
    public Set<Agent> getEdgeLeaveCandidates() {
        return edgeLeaveCandidates;
    }
    
    public boolean isValidOutgoingLane(Lane lane) {
        return lanes.contains(lane);
    }
    
    public int getIndex() {
        return index;
    }
    
    private Set<SimpleEntry<Lane, Lane>> getConnections() {
        Junction end = getEdge().getEnd();
        Set<SimpleEntry<Lane, Lane>> connections = new HashSet<>();
        for (Lane incoming : end.getIncomingLanes()) {
            for (Lane outgoing : incoming.getLanes()) {
                connections.add(new SimpleEntry<Lane, Lane>(incoming, outgoing));
            }
        }
        return connections;
    }
    
    public boolean isBranch() {
        return getLanes().size() > 1;
    }
    
    public boolean isMerging() {
        // all connections on next junction
        Set<SimpleEntry<Lane, Lane>> connections = getConnections();
        // evaluate...
        Collection<Lane> options = getLanes();
        if (options.size() == 0) {
            return false;
        } else if (options.size() > 0) {
            for (Lane option : options) {
                long count = connections.stream().filter(x -> x.getValue().equals(option)).count();
                if (count > 1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Map<Agent, Optional<Lane>> getLaneChangeCandidates() {
        final Map<Agent, Optional<Lane>> changeAgents = new ConcurrentHashMap<>();
        final Set<Agent> laneChangeCandidates = new HashSet<>();
        
        // TODO: this as stream
        for (final Set<Agent> agents : laneAgents.values()) {
            for (final Agent agent : agents) {
                if (agent.isLaneChangeCandidate()) {
                    laneChangeCandidates.add(agent);
                }
            }
        }
        laneChangeCandidates.forEach(agent -> {
            switch (agent.getDecision().getLaneChange()) {
                case RIGHT :
                    changeAgents.put(agent, getRightLane());
                break;
                case LEFT :
                    changeAgents.put(agent, getLeftLane());
                break;
                default :
                    throw new IllegalAccessError("lane change direction");
            }
        });
        return changeAgents;
    }
    
    public Collection<Lane> getLanes() {
        return lanes;
    }
    
    public Optional<Lane> getLeftLane() {
        return getEdge().getLanes().stream().filter(x -> x.index == index + 1).findAny();
    }
    
    public double getLength() {
        return length;
    }
    
    /**
     * Returns the next agents on line in front of a agent on the same lane.
     *
     * @param agent
     *            an agent on the same lane
     * @return the next @{link Agent} on line, empty set if there is none.
     */
    public Set<Agent> getNextAgentsOnLine(final Agent agent) {
        if (agent == null) {
            throw new ArgumentNullException("agent");
        }
        if (agent.getLane() != this) {
            throw new IllegalArgumentException("agent is not on this lane");
        }
        Set<Agent> nextAgentsOnLine = new TreeSet<>();
        try {
            nextAgentsOnLine = getNextAgentsOnLine(agent.getRelativeLanePosition());
        } catch (IllegalArgumentException e) {
            log.info("agent off the line: can't lookup next agents on line");
        }
        
        return nextAgentsOnLine;
    }
    
    /**
     * Returns the next agents on line.
     *
     * @param relativePosition
     *            the relative position on this lane
     * @return the next @{link Agent} on line, empty set if there is none.
     */
    public Set<Agent> getNextAgentsOnLine(final double relativePosition) {
        if (relativePosition < 0 || relativePosition > 1.0) {
            throw new IllegalArgumentException("relative position invalid: " + relativePosition);
        }
        final Entry<Double, Set<Agent>> nextAgentsEntry = laneAgents.higherEntry(relativePosition);
        Set<Agent> nextAgents = new HashSet<>();
        if (nextAgentsEntry != null) {
            nextAgents = nextAgentsEntry.getValue();
        }
        return nextAgents;
    }
    
    public PolyShape getPolyShape() {
        return polyShape;
    }
    
    /**
     * Gets the relative position on this lane from an absolute position in
     * meters.
     *
     * @param absolutePosition
     *            absolute position in meters on this lane
     * @return relative position
     */
    public double getRelativePosition(final double absolutePosition) {
        if (absolutePosition < 0 || absolutePosition > getLength()) {
            throw new IllegalArgumentException("absolutePosition out of bounds");
        }
        return absolutePosition / getLength();
    }
    
    public Optional<Lane> getRightLane() {
        return getEdge().getLanes().stream().filter(x -> x.index == index - 1).findAny();
    }
    
    @Override
    public Lane getSpawnLane() {
        return this;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public boolean goesTo(final Junction junction) {
        return getEdge().getEnd() == junction;
    }
    
    public void removeEdgeLeaveCandidate(final Agent agent) {
        if (agent == null || !edgeLeaveCandidates.contains(agent)) {
            throw new IllegalArgumentException("agent");
        }
        edgeLeaveCandidates.remove(agent);
    }
    
    /**
     * Remove agent from this lane
     *
     * @param agent
     *            agent to remove
     */
    public void removeLaneAgent(final Agent agent) {
        if (agent == null) {
            throw new IllegalArgumentException("agent");
        }
        final Set<Agent> agentsAtPosition = laneAgents.get(agent.getRelativeLanePosition());
        if (agentsAtPosition != null) {
            agentsAtPosition.remove(agent);
        }
        
    }
    
    private Color getColor() {
        String mode = Config.getInstance().getEnum("lane.render.colormode", new String[] { "normal", "density", "timemeanspeed", "spacemeanspeed" });
        if ("normal".equals(mode)) {
            return Color.BLACK;
        }
        if ("density".equals(mode)) {
            return getHeatColor(density, 0.0, 0.14);
        }
        if ("timemeanspeed".equals(mode)) {
            return getHeatColor(timeMeanSpeed, 0.0, 33.3);
        }
        if ("spacemeanspeed".equals(mode)) {
            return getHeatColor(spaceMeanSpeed, 0.0, 33.3);
        }
        throw new RuntimeException("illegal color mode");
    }
    
    @Override
    public void render(final Graphics2D g) {
        g.setStroke(new BasicStroke(3));
        g.setColor(getColor());
        g.draw(polyShape.getShape());
        
        if (LANE_RENDER_INFOS) {
            g.setFont(App.FONT);
            g.setColor(getColor());
            g.drawString(this.toString(), (int) getPosition().getX(), (int) getPosition().getY());
        }
    }
    
    @Override
    public void simulate(final double duration) {
        final NavigableMap<Double, Set<Agent>> oldAgents = new TreeMap<>(laneAgents);
        laneAgents.clear();
        
        List<Agent> allAgents = new LinkedList<>();
        
        // go through agents in order
        while (oldAgents.size() > 0) {
            final Entry<Double, Set<Agent>> entry = oldAgents.pollFirstEntry();
            for (final Agent thisAgent : entry.getValue()) {
                
                allAgents.add(thisAgent);
                
                if (App.getInstance().getSimulation().isAllowCollisions()) {
                    // check for collision with next, if there is a next and
                    // thisAgent was fully moved
                    if (thisAgent.isOnLane() && oldAgents.size() > 0) {
                        final Entry<Double, Set<Agent>> nextEntry = oldAgents.firstEntry();
                        for (final Agent nextAgent : nextEntry.getValue()) {
                            final double distanceLeft = nextAgent.getLanePosition() - thisAgent.getLanePosition() - thisAgent.getVehicle().getLength() / 2 - nextAgent.getVehicle().getLength() / 2;
                            if (nextAgent.isOnLane() && distanceLeft <= 0) {
                                // collision!
                                log.debug(String.format("Collision between agents %d and %d (distance left: %f)", thisAgent.getId(), nextAgent.getId(), distanceLeft));
                                thisAgent.collide();
                                nextAgent.collide();
                                thisAgent.setLanePosition(thisAgent.getLanePosition() + distanceLeft);
                            }
                        }
                    }
                }
                
                // add this agent again
                if (thisAgent.isEdgeLeaveCandidate()) {
                    addEdgeLeaveCandidate(thisAgent);
                } else if (!thisAgent.isRemoveCandidate()) {
                    addLaneAgent(thisAgent);
                }
            }
        }
        
        // collect some statistics informations
        timeMeanSpeed = Statistics.getTimeMeanSpeed(allAgents);
        spaceMeanSpeed = Statistics.getSpaceMeanSpeed(allAgents);
        density = Statistics.getDensity(allAgents.size(), getLength());
    }
    
    @Override
    public String toString() {
        return String.format("Lane{ id: %d, name: %s, density: %.2f, v_sms: %.2f, v_tms: %.2f }", getId(), getName(), density, spaceMeanSpeed, timeMeanSpeed);
    }
}
