package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import ch.bfh.ti.jts.utils.graph.DirectedGraphEdge;

public class Edge extends Element implements DirectedGraphEdge<Edge, Junction> {
    
    private static final long      serialVersionUID  = 1L;
    public static final int        EDGE_RENDER_LAYER = Net.NET_RENDER_LAYER + 1;
    private final Junction         start;
    private final Junction         end;
    private final int              priority;
    private final Collection<Lane> lanes;
    
    public Edge(final String name, final Junction start, final Junction end, final int priority) {
        super(name);
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
    
    @Override
    public Junction getStart() {
        return start;
    }
    
    @Override
    public Junction getEnd() {
        return end;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public Collection<Lane> getLanes() {
        return lanes;
    }
    
    public Lane getFirstLane() {
        return getLanes().stream().sequential().findFirst().orElse(null);
    }
    
    @Override
    public double getLength() {
        double maxLenght = 0.0;
        Optional<Lane> maxLane = lanes.stream().max((x, y) -> {
            return new Double(x.getLength()).compareTo(y.getLength());
        });
        if (maxLane.isPresent()) {
            maxLenght = maxLane.get().getLength();
        }
        return maxLenght;
    }
    
    @Override
    public int getRenderLayer() {
        return EDGE_RENDER_LAYER;
    }
    
    @Override
    public void render(final Graphics2D g) {
        // do nothing
    }
}
