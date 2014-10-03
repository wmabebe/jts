package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;

public class Edge extends Element {
    
    public static final int        EDGE_LAYER = Net.NET_LAYER + 1;
    private final Junction         start;
    private final Junction         end;
    private final int              priority;
    private final Collection<Lane> lanes;
    
    public Edge(final Junction start, final Junction end, final int priority) {
        if (start == null) {
            throw new IllegalArgumentException("start is null");
        }
        if (end == null) {
            throw new IllegalArgumentException("end is null");
        }
        this.start = start;
        this.end = end;
        this.priority = priority;
        lanes = new LinkedList<Lane>();
    }
    
    public Junction getStart() {
        return start;
    }
    
    public Junction getEnd() {
        return end;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public Collection<Lane> getLanes() {
        return lanes;
    }
    
    @Override
    public int getLayer() {
        return EDGE_LAYER;
    }
    
    @Override
    public void render(final Graphics2D g) {
        g.setStroke(new BasicStroke(6));
        g.setColor(Color.BLACK);
        g.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
    }
    
    @Override
    public void simulate(Element oldSelf, Duration duration) {
        // do nothing
    }
}
