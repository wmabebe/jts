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

import ch.bfh.ti.jts.ai.Decision.LaneChangeDirection;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.gui.data.PolyShape;
import ch.bfh.ti.jts.simulation.Simulatable;

public class Lane extends Element implements Simulatable, Renderable {
    
    private static final long                      serialVersionUID      = 1L;
    public final static int                        LANE_RENDER_LAYER     = Edge.EDGE_RENDER_LAYER + 1;
    public final static int                        LANE_SIMULATION_LAYER = Agent.AGENT_SIMULATION_LAYER + 1;
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
     * Unordered agents on this lane.
     */
    private final Set<Agent>                       agents;
    /**
     * Agents on line. Key: RelativePosition, Value: List of @{link Agent}s
     */
    private final NavigableMap<Double, Set<Agent>> laneAgents;
    
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
        agents = new HashSet<>();
        laneAgents = new TreeMap<>();
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
        agents.add(agent);
        Set<Agent> agentsAtPosition = laneAgents.get(agent.getRelativePosition());
        if (agentsAtPosition == null) {
            // position not yet known.
            agentsAtPosition = new HashSet<>();
            laneAgents.put(agent.getRelativePosition(), agentsAtPosition);
        }
        agentsAtPosition.add(agent);
    }
    
    /**
     * Remove agent from this lane
     * 
     * @param agent
     *            agent to remove
     */
    public void removeAgent(final Agent agent) {
        agents.remove(agent);
        Set<Agent> agentsAtPosition = laneAgents.get(agent.getRelativePosition());
        if (agentsAtPosition != null) {
            agentsAtPosition.remove(agent);
        }
    }
    
    /**
     * Returns the next agents on line
     * 
     * @param agent
     *            the relative position on this lane
     * @return the next @{link Agent} on line, null if there is none.
     */
    public Set<Agent> nextAgentsOnLine(final Agent agent) {
        Entry<Double, Set<Agent>> nextAgentsEntry = laneAgents.higherEntry(agent.getRelativePosition());
        Set<Agent> nextAgents = null;
        if (nextAgentsEntry != null) {
            nextAgents = nextAgentsEntry.getValue();
        }
        return nextAgents;
    }
    
    public Map<Agent, Optional<Lane>> getLaneSwitchCandidates() {
        final Map<Agent, Optional<Lane>> switchAgents = new ConcurrentHashMap<>();
        agents.stream().filter(agent -> {
            // agents which want to change and are moving
                return agent.getDecision().getLaneChangeDirection() != LaneChangeDirection.NONE && agent.getVelocity() > 0;
            }).forEach(agent -> {
            switch (agent.getDecision().getLaneChangeDirection()) {
                case RIGHT :
                    switchAgents.put(agent, getRightLane());
                break;
                case LEFT :
                    switchAgents.put(agent, getLeftLane());
                break;
                default :
                    throw new IllegalAccessError("lane change direction");
            }
        });
        return switchAgents;
    }
    
    public Map<Agent, Lane> getLaneLeaveCandidates() {
        final Map<Agent, Lane> leaveAgents = new HashMap<>();
        for (final Agent agent : agents) {
            if (agent.getDistanceToDrive() > 0) {
                final Lane nextJunctionLane = agent.getDecision().getNextJunctionLane();
                if (nextJunctionLane != null && lanes.contains(nextJunctionLane)) {
                    leaveAgents.put(agent, nextJunctionLane);
                } else {
                    // not a valid decision, stop agent.
                    agent.setVelocity(0.0);
                    agent.setDistanceToDrive(0.0);
                    
                }
            }
        }
        return leaveAgents;
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
    
    @Override
    public int getRenderLayer() {
        return LANE_RENDER_LAYER;
    }
    
    public Optional<Lane> getRightLane() {
        return getEdge().getLanes().stream().filter(x -> x.index == index - 1).findAny();
    }
    
    @Override
    public int getSimulationLayer() {
        return LANE_SIMULATION_LAYER;
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
                if (oldAgents.size() > 0 && thisAgent.getDistanceOnLaneLeft() <= 0) {
                    final Entry<Double, Set<Agent>> nextEntry = oldAgents.firstEntry();
                    for (final Agent nextAgent : nextEntry.getValue()) {
                        final double distanceLeft = thisAgent.getPosition().distance(nextAgent.getPosition()) - thisAgent.getVehicle().getLength() / 2 - nextAgent.getVehicle().getLength() / 2;
                        if (distanceLeft <= 0) {
                            // collision!
                            thisAgent.setVelocity(0);
                            nextAgent.setVelocity(0);
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
