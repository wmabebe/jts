package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Collection;
import java.util.LinkedList;

public class Lane extends Element {
    
    private final Edge             edge;
    private final int              index;
    private final double           speed;
    private final double           length;
    private final Shape            shape;
    private final Collection<Lane> lanes;
    
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
    
    @Override
    public void render(final Graphics2D g) {
        g.draw(shape);
    }
}
