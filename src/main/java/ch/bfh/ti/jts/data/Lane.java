package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.gui.data.PolyShape;
import ch.bfh.ti.jts.simulation.Simulatable;

public class Lane extends Element implements Simulatable, Renderable {
    
    private static final long            serialVersionUID      = 1L;
    public final static int              LANE_RENDER_LAYER     = Edge.EDGE_RENDER_LAYER + 1;
    public final static int              LANE_SIMULATION_LAYER = Agent.AGENT_SIMULATION_LAYER + 1;
    private final Edge                   edge;
    private final int                    index;
    private final double                 speed;
    private final double                 length;
    private final PolyShape              polyShape;
    /**
     * Lanes which are connected to this lane (over a junction)
     */
    private final Collection<Lane>       lanes;
    /**
     * Skiplist of agents on the line. Key: Position on line, Value: Agent
     */
    private ConcurrentSkipListSet<Agent> agents;
    
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
        lanes = new LinkedList<Lane>();
        agents = new ConcurrentSkipListSet<Agent>();
    }
    
    public boolean comesFrom(final Junction junction) {
        return getEdge().getStart() == junction;
    }
    
    public ConcurrentSkipListSet<Agent> getAgents() {
        return agents;
    }
    
    public Lane getDecisionLane(final Decision decision) {
        Lane lane = null;
        switch (decision.getLaneChangeDirection()) {
            case RIGHT :
                lane = getRightLane();
            break;
            case LEFT :
                lane = getLeftLane();
            break;
            default :
            case NONE :
                lane = this;
            break;
        }
        // if the requested lane is not there we stay on the same lane
        if (lane == null) {
            lane = this;
        }
        return lane;
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
            if (thisAgent.getLane() == this && agents.size() > 0) {
                final Agent nextAgent = agents.first();
                final double distanceLeft = thisAgent.getPosition().distance(nextAgent.getPosition()) - thisAgent.getVehicle().getLength() / 2 - nextAgent.getVehicle().getLength() / 2;
                if (nextAgent.getLane() == this && distanceLeft <= 0) {
                    // collision!
                    thisAgent.setVelocity(0);
                    nextAgent.setVelocity(0);
                    Logger.getGlobal().log(Level.INFO, "collision happened");
                } else if (distanceLeft <= 0) {
                    Logger.getGlobal().log(Level.INFO, "collision not on same lane");
                }
            }
            agentsBuffer.add(thisAgent);
        }
        agents = agentsBuffer;
    }
}
