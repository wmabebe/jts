package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.graph.DirectedGraphEdge;

public class Edge extends Element implements DirectedGraphEdge<Edge, Junction>, Simulatable, Renderable {
    
    private static final long      serialVersionUID      = 1L;
    public static final int        EDGE_RENDER_LAYER     = Net.NET_RENDER_LAYER + 1;
    public static final int        EDGE_SIMULATION_LAYER = Lane.LANE_SIMULATION_LAYER + 1;
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
    public Junction getEnd() {
        return end;
    }
    
    public Lane getFirstLane() {
        return getLanes().stream().sequential().findFirst().orElse(null);
    }
    
    public Collection<Lane> getLanes() {
        return lanes;
    }
    
    @Override
    public double getLength() {
        double maxLenght = 0.0;
        final Optional<Lane> maxLane = lanes.stream().max((x, y) -> {
            return new Double(x.getLength()).compareTo(y.getLength());
        });
        if (maxLane.isPresent()) {
            maxLenght = maxLane.get().getLength();
        }
        return maxLenght;
    }
    
    public int getPriority() {
        return priority;
    }
    
    @Override
    public int getRenderLayer() {
        return EDGE_RENDER_LAYER;
    }
    
    @Override
    public int getSimulationLayer() {
        return EDGE_SIMULATION_LAYER;
    }
    
    @Override
    public Junction getStart() {
        return start;
    }
    
    @Override
    public void render(final Graphics2D g) {
        // do nothing
    }
    
    @Override
    public void simulate(final double duration) {
        // do lane switching. only for moving agents.
        getLanes().forEach(lane -> {
            lane.getAgents().forEach(agent -> {
                if (agent.getVelocity() > 0) {
                    lane.getAgents().remove(agent);
                    final Lane decisionLane = lane.getDecisionLane(agent.getDecision());
                    decisionLane.getAgents().add(agent);
                    agent.setLane(decisionLane);
                }
            });
        });
    }
}
