package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import ch.bfh.ti.jts.App;
import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.ai.Decision.LaneChangeDirection;
import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.Helpers;

public abstract class Agent extends Element implements Thinkable, Simulatable, Renderable {
    
    private static final long  serialVersionUID       = 1L;
    /**
     * The hue of the agent when driving with maximum velocity. Slower is in the
     * range [0 , AGENT_MAX_VELOCITY_COLOR]. 0.33 : Green
     */
    public final static double AGENT_MAX_VELOCITY_HUE = 0.33;
    private final Decision     decision               = new Decision();
    private Lane               lane;
    /**
     * The velocity of an agent in m/s
     */
    private double             velocity               = 0;
    /**
     * The acceleration of a agent in m/s^2
     */
    private double             acceleration           = 0;
    /**
     * Distance to drive in m
     */
    private double             positionOnLane         = 0;
    /**
     * Vehicle of this agent
     */
    private Vehicle            vehicle;
    /**
     * Optional spawning information of this agent. Can be null.
     */
    private SpawnInfo          spawnInfo;
    
    private boolean            isRemoveCandidate      = false;
    
    public Agent() {
        super("Agent");
    }
    
    public void init(final double positionOnLane, final Vehicle vehicle, final double velocity) {
        init(positionOnLane, vehicle, velocity, null);
    }
    
    public void init(final double positionOnLane, final Vehicle vehicle, final double velocity, SpawnInfo spawnInfo) {
        setPositionOnLane(positionOnLane);
        setVehicle(vehicle);
        setVelocity(velocity);
        setSpawnInfo(spawnInfo);
    }
    
    @Override
    public String toString() {
        return "Agent x: " + positionOnLane + " x/l: " + getRelativePositionOnLane() + " v: " + velocity + " a: " + acceleration;
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
    
    public Lane getLane() {
        return lane;
    }
    
    public void remove() {
        isRemoveCandidate = true;
        getLane().removeEdgeLeaveCandidate(this);
        lane = null;
    }
    
    public boolean isRemoveCandidate() {
        return isRemoveCandidate;
    }
    
    /**
     * The absolute position of the agent on the lane. From the start to the
     * current position of the agent.
     * 
     * @return
     */
    public double getPositionOnLane() {
        return positionOnLane;
    }
    
    public double getRelativePositionOnLane() {
        return positionOnLane / getLane().getLength();
    }
    
    public Point2D getPosition() {
        return getLane().getPolyShape().getRelativePosition(getRelativePositionOnLane());
    }
    
    public double getDistanceOnLaneLeft() {
        return getLane().getLength() - positionOnLane;
    }
    
    private void setPositionOnLane(double positionOnLane) {
        this.positionOnLane = Math.max(positionOnLane, 0);
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    private void setVehicle(final Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle");
        }
        this.vehicle = vehicle;
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    private void setVelocity(final double velocity) {
        this.velocity = Helpers.clamp(velocity, vehicle.getMinVelocity(), vehicle.getMaxVelocity());
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
        setPositionOnLane(getPositionOnLane() - this.lane.getLength());
        this.lane = nextEdgeLane;
    }
    
    public double getDistanceToNextAgent() {
        double oPosition = getLane().getNextAgentsOnLine(this).stream().mapToDouble(x -> x.getPositionOnLane()).min().orElse(0.0);
        double tPosition = getPositionOnLane();
        double delta = oPosition - tPosition;
        return Helpers.clamp(delta, 0.0, Double.MAX_VALUE);
    }
    
    public void setAcceleration(final double acceleration) {
        this.acceleration = Helpers.clamp(acceleration, vehicle.getMinAcceleration(), vehicle.getMaxAcceleration());
    }
    
    @Override
    public int getRenderLayer() {
        return Renderable.KNOWN_CLASSES.get(Agent.class);
    }
    
    @Override
    public int getSimulationLayer() {
        return Simulatable.KNOWN_CLASSES.get(Agent.class);
    }
    
    public boolean isOnLane() {
        //@formatter:off
        return  !isLaneChangeCandidate() 
                && !isEdgeLeaveCandidate();
        // @formatter:on
    }
    
    /**
     * @return @{code true} if agent want's to change lane to an other lane on
     *         the same edge, @{code false} otherwise.
     */
    public boolean isLaneChangeCandidate() {
        //@formatter:off
        return  getVelocity() > 0 
                && (
                        (   
                                getDecision().getLaneChangeDirection() == LaneChangeDirection.RIGHT 
                                && getLane().getRightLane().isPresent()
                        )||(
                                getDecision().getLaneChangeDirection() == LaneChangeDirection.LEFT 
                                && getLane().getLeftLane().isPresent()
                        )
                );
        //@formatter:on
    }
    
    /**
     * @return @{code true} if agent is at end of lane and want's to leave the
     *         edge, @{code false} otherwise
     */
    public boolean isEdgeLeaveCandidate() {
        //@formatter:off
        return  getRelativePositionOnLane() > 1.0
                && getDecision().getNextEdgeLane() != null;
        //@formatter:on
    }
    
    /**
     * This agent collided for some reason with something
     */
    public void collide() {
        setVelocity(0.0);
        
    }
    
    @Override
    public void render(final Graphics2D g) {
        final Point2D position = getPosition();
        final double x = position.getX();
        final double y = position.getY();
        final double orientation = getLane().getPolyShape().getRelativeOrientation(getRelativePositionOnLane());
        final AffineTransform at = AffineTransform.getRotateInstance(orientation);
        g.setStroke(new BasicStroke(1));
        g.setColor(getColor());
        g.translate(x, y);
        g.fill(at.createTransformedShape(vehicle.getShape()));
        if (App.DEBUG) {
            g.setFont(new Font("sans-serif", Font.PLAIN, 4));
            g.scale(1, -1);
            g.drawString("Agent " + getId(), 5, 1);
            g.scale(1, -1);
        }
        g.translate(-x, -y);
    }
    
    @Override
    public void simulate(final double duration) {
        // update acceleration
        setAcceleration(getDecision().getAcceleration());
        // update velocity
        setVelocity(getVelocity() + getAcceleration() * duration);
        // update position
        setPositionOnLane(getPositionOnLane() + getVelocity() * duration);
    }
    
    private void setSpawnInfo(SpawnInfo spawnInfo) {
        this.spawnInfo = spawnInfo;
    }
    
    public SpawnInfo getSpawnInfo() {
        return spawnInfo;
    }
}
