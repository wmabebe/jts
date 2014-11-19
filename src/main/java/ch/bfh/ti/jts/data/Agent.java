package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.Helpers;

public abstract class Agent extends Element implements Thinkable, Simulatable, Renderable {
    
    private static final long  serialVersionUID       = 1L;
    public final static int    AGENT_RENDER_LAYER     = Junction.JUNCTION_RENDER_LAYER + 1;
    public final static int    AGENT_SIMULATION_LAYER = 0;
    /**
     * The hue of the agent when driving with maximum velocity. Slower is in the
     * range [0 , AGENT_MAX_VELOCITY_COLOR]. 0.33 : Green
     */
    public final static double AGENT_MAX_VELOCITY_HUE = 0.33;
    private final Decision     decision               = new Decision();
    private Lane               lane;
    /**
     * The relative position of the agent on the lane (>= 0.0 and <= 1.0)
     */
    private double             relativePosition       = 0;
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
    private double             distanceToDrive        = 0;
    /**
     * Vehicle of this agent
     */
    private Vehicle            vehicle;
    
    public Agent() {
        super(null);
        vehicle = new Vehicle(-20, 20, 0, 33.3 /* 120 km/h */, 1);
    }
    
    @Override
    public String toString() {
        return "Agent x: " + relativePosition + " v: " + velocity + " a: " + acceleration + " dx: " + distanceToDrive;
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
    
    public void setDistanceToDrive(final double distanceToDrive) {
        this.distanceToDrive = distanceToDrive;
    }
    
    public double getDistanceToDrive() {
        return distanceToDrive;
    }
    
    public Lane getLane() {
        return lane;
    }
    
    public Point2D getPosition() {
        return getLane().getPolyShape().getRelativePosition(getRelativePosition());
    }
    
    public double getRelativePosition() {
        return relativePosition;
    }
    
    /**
     * The absolute position of the agent on the lane. From the start to the
     * current position of the agent.
     * 
     * @return
     */
    public double getAbsPosOnLane() {
        return getRelativePosition() * getLane().getLength();
    }
    
    @Override
    public int getRenderLayer() {
        return AGENT_RENDER_LAYER;
    }
    
    @Override
    public int getSimulationLayer() {
        return AGENT_SIMULATION_LAYER;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    public double getDistanceOnLaneLeft() {
        final double lengthLane = getLane().getLength();
        final double distanceOnLaneLeft = lengthLane * (1 - getRelativePosition());
        return distanceOnLaneLeft;
    }
    
    public double getDistanceToNextAgent() {
        double oPosition = getLane().nextAgentsOnLine(this).stream().mapToDouble(x -> x.getAbsPosOnLane()).min().orElse(0.0);
        double tPosition = getAbsPosOnLane();
        double delta = oPosition - tPosition;
        return Helpers.clamp(delta, 0.0, Double.MAX_VALUE);
    }
    
    public void setAcceleration(final double acceleration) {
        // check if out of bounds
        this.acceleration = Helpers.clamp(acceleration, vehicle.getMinAcceleration(), vehicle.getMaxAcceleration());
    }
    
    public void setLane(final Lane lane) {
        this.lane = lane;
    }
    
    public void setRelativePosition(final Double position) {
        if (position < 0 || position > 1.0) {
            throw new IllegalArgumentException("position");
        }
        relativePosition = position;
    }
    
    public void setVehicle(final Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle is null");
        }
        this.vehicle = vehicle;
    }
    
    public void setVelocity(final double velocity) {
        // check if out of bounds
        this.velocity = Helpers.clamp(velocity, vehicle.getMinVelocity(), vehicle.getMaxVelocity());
    }
    
    /**
     * Change velocity based on a specified amount of time. The agents current
     * acceleration is applied.
     *
     * @param duration
     *            the simulation duration
     */
    public void accelerate(final double duration) {
        final double deltaVelocity = getAcceleration() * duration;
        setVelocity(getVelocity() + deltaVelocity);
    }
    
    /**
     * Change position based on a specified amount of time. The agents current
     * velocity is applied.
     *
     * @param duration
     *            the simulation duration
     * @param decision
     *            the agents decision
     */
    public void move(final double duration) {
        // set new driving distance
        distanceToDrive += getVelocity() * duration;
        final double distanceOnLaneLeft = getDistanceOnLaneLeft();
        final double distanceToDriveOnNextLane = Math.max(distanceToDrive - distanceOnLaneLeft, 0.0);
        final double distanceToDriveOnThisLane = distanceToDrive - distanceToDriveOnNextLane;
        setRelativePosition(Helpers.clamp(getRelativePosition() + distanceToDriveOnThisLane / getLane().getLength(), 0.0, 1.0));
        distanceToDrive = distanceToDriveOnNextLane;
        // Logger.getLogger(Agent.class.getName()).info("distanceToDrive: " +
        // distanceToDrive);
    }
    
    @Override
    public void render(final Graphics2D g) {
        final Point2D position = getPosition();
        final double x = position.getX();
        final double y = position.getY();
        final double orientation = getLane().getPolyShape().getRelativeOrientation(getRelativePosition());
        final AffineTransform at = AffineTransform.getRotateInstance(orientation);
        g.setStroke(new BasicStroke(1));
        g.setColor(getColor());
        g.translate(x, y);
        g.fill(at.createTransformedShape(vehicle.getShape()));
        g.translate(-x, -y);
    }
    
    @Override
    public void simulate(final double duration) {
        setAcceleration(getDecision().getAcceleration());
        accelerate(duration);
        move(duration);
    }
}
