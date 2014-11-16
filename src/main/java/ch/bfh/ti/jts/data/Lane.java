package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Logger;

import ch.bfh.ti.jts.ai.Decision.LaneChangeDirection;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.gui.data.PolyShape;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.exceptions.PositionOccupiedException;

public class Lane extends Element implements Simulatable, Renderable {
    
    private static final long      serialVersionUID      = 1L;
    public final static int        LANE_RENDER_LAYER     = Edge.EDGE_RENDER_LAYER + 1;
    public final static int        LANE_SIMULATION_LAYER = Agent.AGENT_SIMULATION_LAYER + 1;
    private final Edge             edge;
    private final int              index;
    private final double           speed;
    private final double           length;
    private final PolyShape        polyShape;
    /**
     * Lanes which are connected to this lane (over a junction)
     */
    private final Collection<Lane> lanes;
    /**
     * Skiplist of agents on the line. Key: Position on line, Value: Agent
     */
    private NavigableSet<Agent>    agents;
    
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
        agents = new TreeSet<>();
    }
    
    public boolean comesFrom(final Junction junction) {
        return getEdge().getStart() == junction;
    }
    
    /**
     * Add a agent to the list of agents on this list.
     * 
     * @param agent
     *            the agent to add
     * @throws PositionOccupiedException
     *             agent position already occupied
     */
    public void addAgent(final Agent agent) throws PositionOccupiedException {
        if (agents.contains(agent)) {
            throw new PositionOccupiedException();
        }
        agents.add(agent);
    }
    
    /**
     * Remove agent from this lane
     * 
     * @param agent
     *            agent to remove
     */
    public void removeAgent(final Agent agent) {
        agents.remove(agent);
    }
    
    /**
     * Returns the next agent on line
     * 
     * @param agent
     *            the relative position on this lane
     * @return the next @{link Agent} on line, null if there is none.
     */
    public Agent nextAgentOnLine(final Agent agent) {
        return agents.higher(agent);
    }
    
    public Map<Agent, Lane> getLaneSwitchCandidates() {
        final Map<Agent, Lane> switchAgents = new ConcurrentHashMap<>();
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
    
    public Edge getEdge() {
        return edge;
    }
    
    public int getIndex() {
        return index;
    }
    
    public Collection<Lane> getLanes() {
        return lanes;
    }
    
    public Lane getLeftLane() {
        return getEdge().getLanes().stream().filter(x -> x.index == index + 1).findAny().orElse(null);
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
    
    public Lane getRightLane() {
        return getEdge().getLanes().stream().filter(x -> x.index == index - 1).findAny().orElse(null);
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
        final ConcurrentSkipListSet<Agent> agentsBuffer = new ConcurrentSkipListSet<Agent>();
        // to through agents in order
        while (agents.size() > 0) {
            final Agent thisAgent = agents.pollFirst();
            if (agents.size() > 0) {
                final Agent nextAgent = agents.first();
                final double distanceLeft = thisAgent.getPosition().distance(nextAgent.getPosition()) - thisAgent.getVehicle().getLength() / 2 - nextAgent.getVehicle().getLength() / 2;
                if (distanceLeft <= 0) {
                    // collision!
                    thisAgent.setVelocity(0);
                    nextAgent.setVelocity(0);
                    Logger.getLogger(Lane.class.getName()).info("collision happened");
                }
            }
            agentsBuffer.add(thisAgent);
        }
        agents = agentsBuffer;
    }
}
