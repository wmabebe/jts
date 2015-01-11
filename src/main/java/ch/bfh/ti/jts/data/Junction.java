package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.exceptions.ArgumentNullException;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.graph.DirectedGraphVertex;
import ch.bfh.ti.jts.utils.graph.GPS;

/**
 * Junctions are the nodes in the street net graph.
 *
 * @author Enteee
 * @author winki
 */
public class Junction extends Element implements SpawnLocation, DirectedGraphVertex<Junction, Edge>, Renderable, Simulatable {
    
    private static final long      serialVersionUID = 1L;
    private static final Logger    log              = LogManager.getLogger(Junction.class);
    
    private final double           x;
    private final double           y;
    private final Shape            shape;
    private final Collection<Edge> edges;
    
    public Junction(final String name, final double x, final double y, final Shape shape) {
        super(name);
        if (shape == null) {
            throw new ArgumentNullException("shape");
        }
        this.x = x;
        this.y = y;
        this.shape = shape;
        edges = new LinkedList<Edge>();
    }
    
    public void addEdge(final Edge edge) {
        edges.add(edge);
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
    
    @Override
    public Collection<Edge> getOutgoingEdges() {
        return edges.stream().filter(edge -> {
            return edge.comesFrom(this);
        }).collect(Collectors.toList());
    }
    
    public Collection<Lane> getOutgoingLanes() {
        return edges.stream().filter(edge -> {
            return edge.comesFrom(this);
        }).flatMap(x -> x.getLanes().stream()).collect(Collectors.toList());
    }
    
    public Collection<Edge> getIncomingEdges() {
        return edges.stream().filter(edge -> {
            return edge.goesTo(this);
        }).collect(Collectors.toList());
    }
    
    public Collection<Lane> getIncomingLanes() {
        return edges.stream().filter(edge -> {
            return edge.goesTo(this);
        }).flatMap(x -> x.getLanes().stream()).collect(Collectors.toList());
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
    public Lane getSpawnLane() {
        final Collection<Edge> edges = getOutgoingEdges();
        if (edges.size() > 0) {
            final Edge edge = edges.stream().findAny().orElse(null);
            if (edge != null) {
                return edge.getFirstLane();
            }
        }
        return null;
    }
    
    @Override
    public Point2D getPosition() {
        return new Point2D.Double(x, y);
    }
    
    @Override
    public void render(final Graphics2D g) {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.BLACK);
        g.fill(shape);
    }
    
    private void switchLane(Agent agent, Lane nextLane) {
        agent.getLane().removeEdgeLeaveCandidate(agent);
        agent.setNextEdgeLane(nextLane);
        nextLane.addLaneAgent(agent);
    }
    
    @Override
    public void simulate(final double duration) {
        // move incoming agents over junction
        edges.stream().filter(edge -> edge.goesTo(this)).forEach(edge -> {
            edge.getEdgeLeaveCandidates().forEach((agent) -> {
                try {
                    // despawn agents
                    final SpawnInfo spawnInfo = agent.getSpawnInfo();
                    if (spawnInfo != null) {
                        final SpawnLocation end = spawnInfo.getEnd();
                        if (equals(end)) {
                            // remove agent
                            agent.remove();
                            return; // break!
                        }
                    }
                } catch (final Exception e) {
                    log.error(String.format("Agent %d can't despawn on junction %s", agent.getId(), getName()), e);
                }
                try {
                    // check switch edge...
                    
                    // 1. next edge lane?
                    Decision decision = agent.getDecision();
                    Lane nextEdgeLane = decision.getTurning();
                    if (nextEdgeLane != null) {
                        // agent wants to switch on a specified lane
                        if (agent.getLane().isValidOutgoingLane(nextEdgeLane)) {
                            // switch to this lane
                            switchLane(agent, nextEdgeLane);
                            return; // break!
                        } else {
                            // not a valid decision
                            log.warn(String.format("Agent %d made no valid decision for next lane", agent.getId()));
                        }
                    }
                    
                    // 2. destination?
                    Junction destination = decision.getDestination();
                    if (destination != null) {
                        // agent has a destination
                        // use gps to get there...
                        Junction lastJunction = agent.getLane().getEdge().getEnd();
                        if (lastJunction == null) {
                            throw new ArgumentNullException("lastJunction");
                        }
                        GPS<Junction, Edge> gps = getNet().getGPS();
                        Edge nextEdge = gps.getNextEdge(lastJunction, destination).orElse(null);
                        if (nextEdge != null) {
                            // take first lane
                            Lane defaultLane = nextEdge.getDefaultLane(agent.getLane());
                            if (defaultLane != null) {
                                // switch to this lane
                                switchLane(agent, defaultLane);
                                return; // break!
                            } else {
                                log.warn("No default lane for this outgoing lane");
                            }
                        } else {
                            log.warn("GPS didn't find a path");
                        }
                        
                    }
                    
                    // 3. no decision?
                    agent.collide();
                    
                } catch (final Exception e) {
                    log.error(String.format("Agent %d can't switch edge on junction %s", agent.getId(), getName()), e);
                }
            });
        });
    }
}
