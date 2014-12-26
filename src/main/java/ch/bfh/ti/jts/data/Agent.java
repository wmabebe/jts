package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.NavigableMap;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bfh.ti.jts.App;
import ch.bfh.ti.jts.Main;
import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.ai.Decision.LaneChangeDirection;
import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.Helpers;

public abstract class Agent extends Element implements Thinkable, Simulatable, Renderable {
    
    private static final long  serialVersionUID               = 1L;
    
    public final static Logger LOG                            = LogManager.getLogger(Agent.class);
    /**
     * The hue of the agent when driving with maximum velocity. Slower is in the
     * range [0 , AGENT_MAX_VELOCITY_COLOR]. 0.33 : Green
     */
    public final static double AGENT_MAX_VELOCITY_HUE         = 0.33;
    /**
     * Duration [s] of change line animation.
     */
    public final static double CHANGE_LINE_ANIMATION_DURATION = 1;
    private final Decision     decision                       = new Decision();
    private Lane               lane;
    /**
     * The velocity of an agent in m/s
     */
    private double             velocity                       = 0;
    /**
     * The acceleration of a agent in m/s^2
     */
    private double             acceleration                   = 0;
    /**
     * Distance to drive in m
     */
    private double             positionOnLane                 = 0;
    /**
     * Vehicle of this agent
     */
    private Vehicle            vehicle;
    /**
     * Optional spawning information of this agent. Can be null.
     */
    private SpawnInfo          spawnInfo;
    
    private boolean            isRemoveCandidate              = false;
    
    public Agent() {
        super("Agent");
    }
    
    /**
     * This agent collided for some reason with something
     */
    public void collide() {
        setVelocity(0.0);
    }
    
    public double getAcceleration() {
        return acceleration;
    }
    
    /**
     * Gets the color of the agent by his velocity.
     *
     * @return color
     */
    private Color getColor() {
        double hue = AGENT_MAX_VELOCITY_HUE * (getVelocity() / vehicle.getMaxVelocity());
        hue = Helpers.clamp(hue, 0.0, 1.0);
        return Color.getHSBColor((float) hue, 1.0f, 1.0f);
    }
    
    @Override
    public Decision getDecision() {
        return decision;
    }
    
    public double getDistanceToNextAgent() {
        final double oPosition = getLane().getNextAgentsOnLine(this).stream().mapToDouble(x -> x.getPositionOnLane()).min().orElse(0.0);
        final double tPosition = getPositionOnLane();
        final double delta = oPosition - tPosition;
        return Helpers.clamp(delta, 0.0, Double.MAX_VALUE);
    }
    
    public Lane getLane() {
        return lane;
    }
    
    @Override
    public Point2D getPosition() {
        return getLane().getPolyShape().getRelativePosition(getRelativePositionOnLane());
    }
    
    /**
     * @return absolute position on the lane. From the start to the current
     *         position of the agent.
     */
    public double getPositionOnLane() {
        return positionOnLane;
    }
    
    public void setPositionOnLane(final double positionOnLane) {
        this.positionOnLane = Math.max(positionOnLane, 0);
    }
    
    /**
     * @return the absolute distance to the end of the line.
     */
    public double getAbsoluteDistanceOnLaneLeft() {
        return getLane().getLength() - getPositionOnLane();
    }
    
    /**
     * @return relative position on lane.
     */
    public double getRelativePositionOnLane() {
        return getPositionOnLane() / getLane().getLength();
    }
    
    @Override
    public int getRenderLayer() {
        return Renderable.KNOWN_CLASSES.get(Agent.class);
    }
    
    @Override
    public int getSimulationLayer() {
        return Simulatable.KNOWN_CLASSES.get(Agent.class);
    }
    
    public SpawnInfo getSpawnInfo() {
        return spawnInfo;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    public void init(final double positionOnLane, final Vehicle vehicle, final double velocity) {
        init(positionOnLane, vehicle, velocity, null);
    }
    
    public void init(final double positionOnLane, final Vehicle vehicle, final double velocity, final SpawnInfo spawnInfo) {
        setPositionOnLane(positionOnLane);
        setVehicle(vehicle);
        setVelocity(velocity);
        setSpawnInfo(spawnInfo);
    }
    
    /**
     * @return @{code true} if agent is at end of lane and want's to leave the
     *         edge, @{code false} otherwise
     */
    public boolean isEdgeLeaveCandidate() {
        return getRelativePositionOnLane() > 1.0;
    }
    
    /**
     * @return @{code true} if agent want's to change lane to an other lane on
     *         the same edge, @{code false} otherwise.
     */
    public boolean isLaneChangeCandidate() {
        //@formatter:off
        return  getVelocity() > 0
                && ( 
                        getDecision().getLaneChangeDirection() == LaneChangeDirection.RIGHT
                        && getLane().getRightLane().isPresent() 
                    ) || (
                        getDecision().getLaneChangeDirection() == LaneChangeDirection.LEFT
                        && getLane().getLeftLane().isPresent()
                   );
        //@formatter:on
    }
    
    public boolean isOnLane() {
        //@formatter:off
        return  !isLaneChangeCandidate()
                && !isEdgeLeaveCandidate();
        // @formatter:on
    }
    
    public boolean isRemoveCandidate() {
        return isRemoveCandidate;
    }
    
    public void remove() {
        isRemoveCandidate = true;
        getLane().removeEdgeLeaveCandidate(this);
        lane = null;
    }
    
    @Override
    public void render(final Graphics2D g) {
        final Point2D position = getPosition();
        final double x = position.getX();
        final double y = position.getY();
        g.setStroke(new BasicStroke(1));
        g.setColor(getColor());
        g.translate(x, y);
        final double orientation = getLane().getPolyShape().getRelativeOrientation(getRelativePositionOnLane());
        g.fill(AffineTransform.getRotateInstance(orientation).createTransformedShape(vehicle.getShape()));
        if (Main.DEBUG) {
            g.setFont(new Font("sans-serif", Font.PLAIN, 4));
            g.scale(1, -1);
            g.drawString("Agent " + getId(), 5, 1);
            g.scale(1, -1);
        }
        g.translate(-x, -y);
    }
    
    @Override
    public void render(final Graphics2D g, final NavigableMap<Double, Net> simulationStates) {
        final Point2D position = getPosition();
        final double x = position.getX();
        final double y = position.getY();
        double xChangeLaneShift = 0;
        double yChangeLaneShift = 0;
        g.translate(x, y);
        final double wallClockTime = App.getInstance().getSimulation().getWallClockTime();
        // check old simulation states
        Optional<Element.ElementInTime> lastAgentStateBeforeLaneChange = simulationStates.headMap(wallClockTime).values().stream().map(oldNet -> {
            return new Element.ElementInTime(oldNet.getSimulationTime(), oldNet.getElement(getId()));
        }).filter(oldAgentInTime -> {
            final Agent oldAgent = (Agent) oldAgentInTime.getElement();
            // lane changed on same edge?
            // @formatter:off
            return  oldAgent != null
                    && (
                            (
                                    getLane().getLeftLane().isPresent()
                                    && oldAgent.getLane().getId() == getLane().getLeftLane().get().getId()
                             ) || ( 
                                     getLane().getRightLane().isPresent()
                                     && oldAgent.getLane().getId() == getLane().getRightLane().get().getId()
                             )
                    );
            // @formatter:on
                }).sorted().findFirst();
        if (lastAgentStateBeforeLaneChange.isPresent()) {
            final double lastTimeBeforeChange = lastAgentStateBeforeLaneChange.get().getTime();
            final Agent lastAgentStateBeforeChange = (Agent) lastAgentStateBeforeLaneChange.get().getElement();
            final double lastLaneChangeRelativeTime = wallClockTime - lastTimeBeforeChange;
            if (lastLaneChangeRelativeTime < CHANGE_LINE_ANIMATION_DURATION) {
                final double changeLaneFactor = 1 - lastLaneChangeRelativeTime / CHANGE_LINE_ANIMATION_DURATION;
                xChangeLaneShift = changeLaneFactor * (lastAgentStateBeforeChange.getPosition().getX() - getPosition().getX());
                yChangeLaneShift = changeLaneFactor * (lastAgentStateBeforeChange.getPosition().getY() - getPosition().getY());
                if (Main.DEBUG) {
                    Color c = g.getColor();
                    g.setColor(Color.GREEN);
                    g.setStroke(new BasicStroke(.5f));
                    g.drawLine(0, 0, (int) xChangeLaneShift, (int) yChangeLaneShift);
                    g.setColor(c);
                }
                // g.translate(xChangeLaneShift, yChangeLaneShift);
            }
        }
        g.translate(-x, -y);
        Renderable.super.render(g, simulationStates);
        // g.translate(-xChangeLaneShift, -yChangeLaneShift);
    }
    
    public void setAcceleration(final double acceleration) {
        this.acceleration = Helpers.clamp(acceleration, vehicle.getMinAcceleration(), vehicle.getMaxAcceleration());
    }
    
    public void setLane(final Lane lane) {
        this.lane = lane;
    }
    
    /**
     * Sets a new lane of this agent and sets{@link Agent#positionOnLane}
     * relative to new edge lane
     *
     * @param nextEdgeLane
     */
    public void setNextEdgeLane(final Lane nextEdgeLane) {
        if (nextEdgeLane == null) {
            throw new IllegalArgumentException("nextEdgeLane");
        }
        setPositionOnLane(getPositionOnLane() - lane.getLength());
        lane = nextEdgeLane;
        if (getPositionOnLane() > getLane().getLength()) {
            throw new RuntimeException("Position is greater than the length of the lane");
        }
    }
    
    private void setSpawnInfo(final SpawnInfo spawnInfo) {
        this.spawnInfo = spawnInfo;
    }
    
    private void setVehicle(final Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle");
        }
        this.vehicle = vehicle;
    }
    
    private void setVelocity(final double velocity) {
        this.velocity = Helpers.clamp(velocity, vehicle.getMinVelocity(), vehicle.getMaxVelocity());
    }
    
    @Override
    public void simulate(final double duration) {
        // update acceleration
        setAcceleration(getDecision().getAcceleration());
        final double oldVelocity = getVelocity();
        // update velocity
        setVelocity(oldVelocity + getAcceleration() * duration);
        // update position
        setPositionOnLane(getPositionOnLane() + (oldVelocity + getVelocity()) / 2 * duration);
    }
    
    @Override
    public String toString() {
        return "Agent x: " + positionOnLane + " l.rel(x): " + getRelativePositionOnLane() + " v: " + velocity + " a: " + acceleration;
    }
}
