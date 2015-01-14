package ch.bfh.ti.jts.data;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bfh.ti.jts.exceptions.ArgumentNullException;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.Helpers;
import ch.bfh.ti.jts.utils.graph.DirectedGraphEdge;

/**
 * Edges are connections between junctions and contains one or multiple lanes.
 *
 * @author Enteee
 * @author winki
 */
public class Edge extends Element implements SpawnLocation, DirectedGraphEdge<Edge, Junction>, Simulatable, Renderable {
    
    private static final long      serialVersionUID = 1L;
    private static final Logger    log              = LogManager.getLogger(Edge.class);
    
    private final Junction         start;
    private final Junction         end;
    /**
     * A priority of this lane. 1 := Min priority, INT.MAX:= Max priority.
     */
    private final int              priority;
    private final Collection<Lane> lanes;
    
    public Edge(final String name, final Junction start, final Junction end, final int priority) {
        super(name);
        if (start == null) {
            throw new ArgumentNullException("start");
        }
        if (end == null) {
            throw new ArgumentNullException("end");
        }
        this.start = start;
        this.start.addEdge(this);
        this.end = end;
        this.end.addEdge(this);
        this.priority = Helpers.clamp(priority, 1, Integer.MAX_VALUE);
        lanes = new LinkedList<Lane>();
    }
    
    public void addLane(final Lane lane) {
        lanes.add(lane);
    }
    
    public Set<Agent> getEdgeLeaveCandidates() {
        return lanes.stream().flatMap(x -> x.getEdgeLeaveCandidates().stream()).collect(Collectors.toSet());
    }
    
    @Override
    public Junction getEnd() {
        return end;
    }
    
    public Lane getFirstLane() {
        return getLanes().stream().sequential().findFirst().orElse(null);
    }
    
    public Lane getDefaultLane(Lane current) {
        return current.getLanes().stream().filter(x -> x.comesFrom(current.getEdge().getEnd())).findFirst().orElse(null);
    }
    
    public Collection<Lane> getLanes() {
        return lanes;
    }
    
    public int getPriority() {
        return priority;
    }
    
    @Override
    public Point2D getPosition() {
        Point2D start = getStart().getPosition();
        Point2D end = getEnd().getPosition();
        return Helpers.pointBetween(start, end);
    }
    
    @Override
    public double getDistance(Point2D coordinates) {
        return Helpers.distancePointToLine(coordinates, new Line2D.Double(getStart().getPosition(), getEnd().getPosition()));
    }
    
    @Override
    public Lane getSpawnLane() {
        return getFirstLane();
    }
    
    @Override
    public Junction getStart() {
        return start;
    }
    
    @Override
    public double getWeight() {
        double maxLenght = Double.POSITIVE_INFINITY;
        final Optional<Lane> maxLane = lanes.stream().max((x, y) -> {
            return new Double(x.getLength()).compareTo(y.getLength());
        });
        if (maxLane.isPresent()) {
            maxLenght = maxLane.get().getLength();
        }
        return maxLenght / getPriority();
    }
    
    @Override
    public void render(final Graphics2D g) {
        // do nothing
    }
    
    @Override
    public void simulate(final double duration) {
        switchLane();
    }
    
    /**
     * Agents switch lane.
     */
    private void switchLane() {
        getLanes().forEach(lane -> {
            lane.getLaneChangeCandidates().forEach((agent, changeLane) -> {
                if (!agent.isRemoveCandidate()) {
                    // only if agent isn't already removed
                    try {
                        // lane switch possible?
                        if (changeLane.isPresent()) {
                            agent.setLane(changeLane.get());
                            changeLane.get().addLaneAgent(agent);
                            lane.removeLaneAgent(agent);
                        } else {
                            agent.remove();
                            log.warn(String.format("Agent %d was removed due to an invalid lane change information", agent.getId()));
                        }
                    } catch (final Exception e) {
                        log.error(String.format("Agent %d can't switch lane", agent.getId()), e);
                    }
                }
            });
        });
    }
    
    @Override
    public String toString() {
        return String.format("Edge{ id: %d }", getId());
    }
}
