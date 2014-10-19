package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;

public class Edge extends Element {
    
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
    
    public Lane getFirstLane() {
        return getLanes().stream().sequential().findFirst().orElse(null);
    }
    
    public boolean goesTo(final Junction junction) {
        return getEnd() == junction;
    }
    
    public boolean comesFrom(final Junction junction) {
        return getStart() == junction;
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
