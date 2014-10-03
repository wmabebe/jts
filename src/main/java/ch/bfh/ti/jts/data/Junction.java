package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;

public class Junction extends Element {
    
    public final static int        JUNCTION_LAYER = Edge.EDGE_LAYER + 1;
    private final double           x;
    private final double           y;
    private final Shape            shape;
    private final Collection<Edge> edges;
    
    public Junction(final double x, final double y, final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("shape is null");
        }
        this.x = x;
        this.y = y;
        this.shape = shape;
        edges = new LinkedList<Edge>();
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public Collection<Edge> getEdges() {
        return edges;
    }
    
    @Override
    public int getLayer() {
        return JUNCTION_LAYER;
    }
    
    @Override
    public void render(final Graphics2D g) {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.BLACK);
        g.fill(shape);
    }
    
    @Override
    public void simulate(Element oldSelf, Duration duration) {
        // do nothing
    }
}
