package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.Helpers;

public abstract class Agent extends Element implements Thinkable, Simulatable {
    
    public final static int AGENT_RENDER_LAYER     = Junction.JUNCTION_RENDER_LAYER + 1;
    public final static int AGENT_SIMULATION_LAYER = 0;
    private Lane            lane;
    /**
     * The relative position of the agent on the lane (>= 0.0 and < 1.0)
     */
    private double          position               = 0;
    /**
     * The velocity of an agent in m/s
     */
    private double          velocity               = 0;
    /**
     * The acceleration of a agent in m/s^2
     */
    private double          acceleration           = 0;
    private final Vehicle   vehicle;
    
    public Agent() {
        this.vehicle = new Vehicle(-20, 20, 0, 33.3 /* 120 km/h */);
    }
    
    public void setLane(final Lane lane) {
        lane.getAgents().remove(this);
        this.lane = lane;
        lane.getAgents().add(this);
    }
    
    public Lane getLane() {
        return lane;
    }
    
    public void setPosition(final Double position) {
        if (position < 0 || position > 1.0) {
            throw new IllegalArgumentException("position");
        }
        lane.getAgents().remove(this);
        this.position = position;
        lane.getAgents().add(this);
    }
    
    public double getPosition() {
        return position;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    private double getX() {
        return getLane().getPolyShape().getRelativePosition(getPosition()).getX();
    }
    
    private double getY() {
        return getLane().getPolyShape().getRelativePosition(getPosition()).getY();
    }
    
    public Junction getNextJunction() {
        return getLane().getEdge().getEnd();
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
    public void move(final double duration, final Decision decision) {
        double deltaDistance = getVelocity() * duration;
        followLane(deltaDistance, decision);
    }
    
    private void followLane(final double distanceToDrive, final Decision decision) {
        double lengthLane = getLane().getLength();
        double distanceOnLaneLeft = lengthLane * (1 - getPosition());
        if (distanceToDrive <= distanceOnLaneLeft) {
            // stay on this lane
            setPosition(getPosition() + distanceToDrive / lengthLane);
        } else {
            // pass junction and switch to an other lane
            double distanceToDriveOnNewLane = distanceToDrive - distanceOnLaneLeft;
            final Lane nextLane = decision.getNextJunctionLane();
            if (nextLane == null) {
                throw new RuntimeException("Agent didn't decide where to go.");
            }
            setPosition(0.0);
            setLane(nextLane);
            followLane(distanceToDriveOnNewLane, decision);
        }
    }
    
    @Override
    public int getRenderLayer() {
        return AGENT_RENDER_LAYER;
    }
    
    /**
     * Gets the color of the agent by his velocity.
     *
     * @return color
     */
    private Color getColor() {
        double hue = 0.33 - 0.33 * (getVelocity() / vehicle.getMaxVelocity());
        hue = Helpers.clamp(hue, 0.0, 1.0);
        return Color.getHSBColor((float) hue, 1.0f, 1.0f);
    }
    
    @Override
    public void render(final Graphics2D g) {
        final double x = getX();
        final double y = getY();
        g.setStroke(new BasicStroke(1));
        g.setColor(getColor());
        g.translate(x, y);
        g.fill(vehicle.getShape());
        g.translate(-x, -y);
    }
    
    @Override
    public int getSimulationLayer() {
        return AGENT_SIMULATION_LAYER;
    }
    
    @Override
    public void simulate(final double duration, final Decision decision) {
        setAcceleration(decision.getAcceleration());
        accelerate(duration);
        move(duration, decision);
    }
}
