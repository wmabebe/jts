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

public abstract class Agent extends Element implements Thinkable, Simulatable, Renderable, Comparable<Agent> {
    
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
    double                     distanceToDrive;
    private Vehicle            vehicle;
    
    public Agent() {
        super(null);
        this.vehicle = new Vehicle(-20, 20, 0, 33.3 /* 120 km/h */, 1);
    }
    
    @Override
    public Decision getDecision() {
        return decision;
    }
    
    public void setVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle is null");
        }
        this.vehicle = vehicle;
    }
    
    public Lane getLane() {
        return lane;
    }
    
    public void setLane(final Lane lane) {
        this.lane = lane;
    }
    
    @Override
    public int compareTo(Agent a) {
        return new Double(getRelativePosition()).compareTo(a.getRelativePosition());
    }
    
    public void setRelativePosition(final Double position) {
        if (position < 0 || position > 1.0) {
            throw new IllegalArgumentException("position");
        }
        this.relativePosition = position;
    }
    
    public double getRelativePosition() {
        return relativePosition;
    }
    
    public Point2D getPosition() {
        return getLane().getPolyShape().getRelativePosition(getRelativePosition());
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public void setVelocity(final double velocity) {
        // check if out of bounds
        this.velocity = Helpers.clamp(velocity, vehicle.getMinVelocity(), vehicle.getMaxVelocity());
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    public double getAcceleration() {
        return acceleration;
    }
    
    public void setAcceleration(final double acceleration) {
        // check if out of bounds
        this.acceleration = Helpers.clamp(acceleration, vehicle.getMinAcceleration(), vehicle.getMaxAcceleration());
    }
    
    /**
     * Change velocity based on a specified amount of time. The agents current
     * acceleration is applied.
     * 
     * @param duration
     *            the simulation duration
     */
    public void accelerate(final double duration) {
        double deltaVelocity = getAcceleration() * duration;
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
        double lengthLane = getLane().getLength();
        double distanceOnLaneLeft = lengthLane * (1 - getRelativePosition());
        double distanceToDriveOnNextLane = Helpers.clamp(distanceToDrive - distanceOnLaneLeft, 0.0, Double.MAX_VALUE);
        double distanceToDriveOnThisLane = distanceToDrive - distanceToDriveOnNextLane;
        // move
        // TODO: 0.999 is a hotfix for index out of bounds in polyshape, set
        // back to 1.0
        setRelativePosition(Helpers.clamp(getRelativePosition() + distanceToDriveOnThisLane / lengthLane, 0.0, 0.999));
        distanceToDrive = distanceToDriveOnNextLane;
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
    public int getRenderLayer() {
        return AGENT_RENDER_LAYER;
    }
    
    @Override
    public void render(final Graphics2D g) {
        final Point2D position = getPosition();
        final double x = position.getX();
        final double y = position.getY();
        double orientation = getLane().getPolyShape().getRelativeOrientation(getRelativePosition());
        AffineTransform at = AffineTransform.getRotateInstance(orientation);
        g.setStroke(new BasicStroke(1));
        g.setColor(getColor());
        g.translate(x, y);
        g.fill(at.createTransformedShape(vehicle.getShape()));
        g.translate(-x, -y);
    }
    
    @Override
    public int getSimulationLayer() {
        return AGENT_SIMULATION_LAYER;
    }
    
    @Override
    public void simulate(final double duration) {
        setAcceleration(getDecision().getAcceleration());
        accelerate(duration);
        move(duration);
    }
}
