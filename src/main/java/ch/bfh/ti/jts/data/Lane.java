package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListSet;

public class Lane extends Element {
    
    public final static int                    LANE_LAYER = Junction.JUNCTION_LAYER + 1;
    private final Edge                         edge;
    private final int                          index;
    private final double                       speed;
    private final double                       length;
    private final Shape                        shape;
    private final Collection<Lane>             lanes;
    /**
     * Skiplist of agents on the line. Key: Position on line, Value: Agent
     */
    private final ConcurrentSkipListSet<Agent> agents;
    /**
     * A comperator for Agents on a Line
     *
     * @author ente
     */
    private class AgentLineComperator implements Comparator<Agent> {
        
        @Override
        public int compare(final Agent a1, final Agent a2) {
            return new Double(a1.getPosition()).compareTo(a2.getPosition());
        }
    }
    
    public Lane(final Edge edge, final int index, final double speed, final double length, final Shape shape) {
        if (edge == null) {
            throw new IllegalArgumentException("edge is null");
        }
        if (shape == null) {
            throw new IllegalArgumentException("shape is null");
        }
        this.edge = edge;
        this.index = index;
        this.speed = speed;
        this.length = length;
        this.shape = shape;
        lanes = new LinkedList<Lane>();
        agents = new ConcurrentSkipListSet<Agent>(new AgentLineComperator());
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
    
    public boolean goesTo(final Junction junction) {
        return getEdge().getEnd() == junction;
    }
    
    public boolean comesFrom(final Junction junction) {
        return getEdge().getStart() == junction;
    }
    
    @Override
    public int getLayer() {
        return LANE_LAYER;
    }
    
    public ConcurrentSkipListSet<Agent> getAgents() {
        return agents;
    }
    
    @Override
    public void render(final Graphics2D g) {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.LIGHT_GRAY);
        g.draw(shape);
    }
}
