package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.bfh.ti.jts.gui.data.PolyShape;
import ch.bfh.ti.jts.simulation.Simulatable;

public class Lane extends Element implements Simulatable {
    
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
        this.index = index;
        this.speed = speed;
        this.length = length;
        this.polyShape = polyShape;
        this.lanes = new LinkedList<Lane>();
        this.agents = new ConcurrentSkipListSet<Agent>();
    }
    
    public Edge getEdge() {
        return edge;
    }
    
    public int getIndex() {
        return index;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public double getLength() {
        return length;
    }
    
    public Collection<Lane> getLanes() {
        return lanes;
    }
    
    public PolyShape getPolyShape() {
        return polyShape;
    }
    
    public boolean goesTo(final Junction junction) {
        return getEdge().getEnd() == junction;
    }
    
    public boolean comesFrom(final Junction junction) {
        return getEdge().getStart() == junction;
    }
    
    public Lane getLeftLane() {
        return getEdge().getLanes().stream().filter(x -> x.index == index + 1).findAny().orElse(null);
    }
    
    public Lane getRightLane() {
        return getEdge().getLanes().stream().filter(x -> x.index == index - 1).findAny().orElse(null);
    }
    
    public ConcurrentSkipListSet<Agent> getAgents() {
        return agents;
    }
    
    @Override
    public int getRenderLayer() {
        return LANE_RENDER_LAYER;
    }
    
    @Override
    public int getSimulationLayer() {
        return LANE_SIMULATION_LAYER;
    }
    
    @Override
    public void simulate(double duration) {
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
                    // Logger.getGlobal().log(Level.INFO, "collision happened");
                } else if (distanceLeft <= 0) {
                    Logger.getGlobal().log(Level.INFO, "collision not on same lane");
                }
            }
            agentsBuffer.add(thisAgent);
        }
        agents = agentsBuffer;
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
}
