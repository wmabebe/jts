package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.gui.data.PolyShape;
import ch.bfh.ti.jts.simulation.Simulatable;

public class Lane extends Element implements Simulatable, Renderable {
    
    private static final long                      serialVersionUID = 1L;
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
    
    public Lane(final String name, final Edge edge, final int index, final double speed, final double length, final PolyShape polyShape) {
        super(name);
        if (edge == null) {
            throw new IllegalArgumentException("edge is null");
        }
        if (polyShape == null) {
            throw new IllegalArgumentException("polyShape is null");
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
    
    public boolean comesFrom(final Junction junction) {
        return getEdge().getStart() == junction;
    }
    
    /**
     * Add a agent to the list of agents on this list.
     * 
     * @param agent
     *            the agent to add
     */
    public void addAgent(final Agent agent) {
        if (agent.isEdgeLeaveCandidate()) {
            edgeLeaveCandidates.add(agent);
        } else {
            Set<Agent> agentsAtPosition = laneAgents.get(agent.getRelativePositionOnLane());
            if (agentsAtPosition == null) {
                // position not yet known.
                agentsAtPosition = new HashSet<>();
                laneAgents.put(agent.getRelativePositionOnLane(), agentsAtPosition);
            }
            agentsAtPosition.add(agent);
            
        }
    }
    
    /**
     * Remove agent from this lane
     * 
     * @param agent
     *            agent to remove
     */
    public void removeAgent(final Agent agent) {
        if (agent.isEdgeLeaveCandidate()) {
            edgeLeaveCandidates.remove(agent);
        } else {
            Set<Agent> agentsAtPosition = laneAgents.get(agent.getRelativePositionOnLane());
            if (agentsAtPosition != null) {
                agentsAtPosition.remove(agent);
            }
        }
    }
    
    /**
     * Returns the next agents on line.
     * 
     * @param agent
     *            the relative position on this lane
     * @return the next @{link Agent} on line, null if there is none.
     */
    public Set<Agent> nextAgentsOnLine(final Agent agent) {
        Entry<Double, Set<Agent>> nextAgentsEntry = laneAgents.higherEntry(agent.getRelativePositionOnLane());
        Set<Agent> nextAgents = new HashSet<>();
        if (nextAgentsEntry != null) {
            nextAgents = nextAgentsEntry.getValue();
        }
        return nextAgents;
    }
    
    public Map<Agent, Optional<Lane>> getLaneChangeCandidates() {
        final Map<Agent, Optional<Lane>> changeAgents = new ConcurrentHashMap<>();
        final Set<Agent> laneChangeCandidates = new HashSet<>();
        // TODO: this as stream
        for (Set<Agent> agents : laneAgents.values()) {
            for (Agent agent : agents) {
                if (agent.isLaneChangeCandidate()) {
                    laneChangeCandidates.add(agent);
                }
                
            }
            
        }
        laneChangeCandidates.forEach(agent -> {
            switch (agent.getDecision().getLaneChangeDirection()) {
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
    
    public Map<Agent, Lane> getEdgeLeaveCandidates() {
        final Map<Agent, Lane> edgeLeaveAgents = new HashMap<>();
        for (final Agent agent : edgeLeaveCandidates) {
            final Lane nextEdgeLane = agent.getDecision().getNextEdgeLane();
            if (nextEdgeLane != null && lanes.contains(nextEdgeLane)) {
                edgeLeaveAgents.put(agent, nextEdgeLane);
            } else {
                // not a valid decision, stop agent.
                agent.collide();
            }
        }
        return edgeLeaveAgents;
    }
    
    public Edge getEdge() {
        return edge;
    }
    
    public int getIndex() {
        return index;
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
    
    public PolyShape getPolyShape() {
        return polyShape;
    }
    
    public Optional<Lane> getRightLane() {
        return getEdge().getLanes().stream().filter(x -> x.index == index - 1).findAny();
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public boolean goesTo(final Junction junction) {
        return getEdge().getEnd() == junction;
    }
    
    @Override
    public void render(final Graphics2D g) {
        g.setStroke(new BasicStroke(4));
        g.setColor(Color.WHITE);
        g.draw(polyShape.getShape());
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.BLACK);
        g.draw(polyShape.getShape());
    }
    
    @Override
    public void simulate(final double duration) {
        final NavigableMap<Double, Set<Agent>> oldAgents = new TreeMap<>(laneAgents);
        laneAgents.clear();
        // go through agents in order
        while (oldAgents.size() > 0) {
            final Entry<Double, Set<Agent>> entry = oldAgents.pollFirstEntry();
            for (final Agent thisAgent : entry.getValue()) {
                // check for collision with next, if there is a next and
                // thisAgent was fully moved
                if (thisAgent.isOnLane() && oldAgents.size() > 0) {
                    final Entry<Double, Set<Agent>> nextEntry = oldAgents.firstEntry();
                    for (final Agent nextAgent : nextEntry.getValue()) {
                        final double distanceLeft = thisAgent.getPosition().distance(nextAgent.getPosition()) - thisAgent.getVehicle().getLength() / 2 - nextAgent.getVehicle().getLength() / 2;
                        if (nextAgent.isOnLane() && distanceLeft <= 0) {
                            // collision!
                            thisAgent.collide();
                            nextAgent.collide();
                            Logger.getLogger(Lane.class.getName()).info("Collision: [" + thisAgent + "] <->[" + nextAgent + "]");
                        }
                    }
                }
                // add this agent again
                addAgent(thisAgent);
            }
        }
    }
}
