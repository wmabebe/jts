package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.ai.Thinkable;
import ch.bfh.ti.jts.simulation.Simulatable;
import ch.bfh.ti.jts.utils.Helpers;

public abstract class Agent extends Element implements Thinkable, Simulatable {
    
    /**
     * Max velocity of agent (inclusive) [m/s]
     */
    private static final double MAX_VELOCITY     = 22.2;
    /**
     * Minimal velocity of agent (inclusive) [m/s], 0 := agent can't reverse.
     */
    private static final double MIN_VELOCITY     = 0;
    /**
     * Max acceleration of agent (inclusive) [m/s^^]
     */
    public static final double  MAX_ACCELERATION = 5.0;
    /**
     * Minimal acceleration of agent (inclusive) [m/s^2]
     */
    public static final double  MIN_ACCELERATION = -5.0;
    public final static int     AGENT_LAYER      = Junction.JUNCTION_LAYER + 1;
    private final static double size             = 3.0;
    private final static Shape  shape            = new Ellipse2D.Double(-size / 2, -size / 2, size, size);
    private Lane                lane;
    /**
     * The relative position of the agent on the lane (>= 0.0 and < 1.0)
     */
    private double              position         = 0;
    /**
     * The velocity of an agent in m/s
     */
    private double              velocity         = 0;
    
    public Agent() {
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
        this.position = position;
    }
    
    public double getPosition() {
        return position;
    }
    
    private double getX() {
        final Junction start = getLane().getEdge().getStart();
        final Junction end = getLane().getEdge().getEnd();
        return start.getX() + getPosition() * (end.getX() - start.getX());
    }
    
    private double getY() {
        final Junction start = getLane().getEdge().getStart();
        final Junction end = getLane().getEdge().getEnd();
        return start.getY() + getPosition() * (end.getY() - start.getY());
    }
    
    public void setVelocity(final double velocity) {
        // check if out of bounds
        this.velocity = Helpers.clamp(velocity, MIN_VELOCITY, MAX_VELOCITY);
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    @Override
    public int getLayer() {
        return AGENT_LAYER;
    }
    
    /**
     * Gets the color of the agent by his velocity.
     *
     * @return color
     */
    private Color getColor() {
        double hue = 0.33 - 0.33 * (getVelocity() / MAX_VELOCITY);
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
        g.fill(shape);
        g.translate(-x, -y);
    }
    
    @Override
    public void simulate(final double duration, final Decision decision) {
        setVelocity(getVelocity() + decision.getAcceleration() * duration);
        followLane(getLane(), getVelocity() * duration, decision);
    }
    
    private void followLane(final Lane currentLane, final double distanceToDrive, final Decision decision) {
        double lengthLane = currentLane.getLength();
        double distanceOnLaneLeft = lengthLane * (1 - getPosition());
        if (distanceToDrive <= distanceOnLaneLeft) {
            // stay on this lane
            setLane(currentLane);
            setPosition(getPosition() + distanceToDrive / lengthLane);
        } else {
            // pass junction and switch to an other lane
            double distanceToDriveOnNewLane = distanceToDrive - distanceOnLaneLeft;
            final Lane nextLane = decision.getNextJunctionLane();
            if (nextLane == null) {
                throw new RuntimeException("Agent didn't decide where to go.");
            }
            setPosition(0.0);
            followLane(nextLane, distanceToDriveOnNewLane, decision);
        }
    }
}
