package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.graph.DirectedGraphVertex;

public class Junction extends Element implements DirectedGraphVertex<Junction, Edge>, Renderable, Simulatable {
    
    private static final long      serialVersionUID          = 1L;
    public final static int        JUNCTION_RENDER_LAYER     = Lane.LANE_RENDER_LAYER + 1;
    public final static int        JUNCTION_SIMULATION_LAYER = Edge.EDGE_SIMULATION_LAYER + 1;
    private final double           x;
    private final double           y;
    private final Shape            shape;
    private final Collection<Edge> edges;
    
    public Junction(final String name, final double x, final double y, final Shape shape) {
        super(name);
        if (shape == null) {
            throw new IllegalArgumentException("shape is null");
        }
        this.x = x;
        this.y = y;
        this.shape = shape;
        edges = new LinkedList<Edge>();
    }
    
    @Override
    public Optional<Edge> getEdgeBetween(final Junction vertex) {
        Optional<Edge> edgeBetween = Optional.empty();
        for (final Edge edge : getOutgoingEdges()) {
            if (edge.goesTo(vertex)) {
                edgeBetween = Optional.of(edge);
                break;
            }
        }
        return edgeBetween;
    }
    
    public Collection<Edge> getEdges() {
        return edges;
    }
    
    @Override
    public Collection<Edge> getOutgoingEdges() {
        return getEdges().stream().filter(x -> {
            return x.comesFrom(this);
        }).collect(Collectors.toList());
    }
    
    @Override
    public Collection<Junction> getReachableVertices() {
        final Collection<Junction> neighbours = new HashSet<>();
        getOutgoingEdges().forEach(x -> {
            neighbours.add(x.getEnd());
        });
        return neighbours;
    }
    
    @Override
    public int getRenderLayer() {
        return JUNCTION_RENDER_LAYER;
    }
    
    @Override
    public int getSimulationLayer() {
        return JUNCTION_SIMULATION_LAYER;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    @Override
    public void render(final Graphics2D g) {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.BLACK);
        g.fill(shape);
    }
    
    @Override
    public void simulate(final double duration) {
        // move incoming agents over junction
        getEdges().stream().filter(edge -> edge.goesTo(this)).forEach(edge -> {
            edge.getLanes().forEach(lane -> {
                for (final Agent agent : lane.getAgents().descendingSet()) {
                    // did we reach a agent which has no distance to drive left:
                    // nothing to do here.
                    if (agent.getDistanceToDrive() > 0) {
                        final Lane nextJunctionLane = agent.getDecision().getNextJunctionLane();
                        if (nextJunctionLane != null) {
                            lane.getAgents().remove(agent);
                            agent.setRelativePosition(0.0);
                            agent.setLane(nextJunctionLane);
                            nextJunctionLane.getAgents().add(agent);
                        }
                    }
                }
            });;
        });
        
    }
}
