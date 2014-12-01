package ch.bfh.ti.jts.data;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.Serializable;

public class Vehicle implements Serializable {

    private static final Shape buildShape() {
        final Path2D path = new Path2D.Double();
        path.moveTo(0.5, 0.0);
        path.lineTo(-0.5, 0.5);
        path.lineTo(-0.5, -0.5);
        path.closePath();
        return path;
    }
    private static final long  serialVersionUID = 1L;
    /**
     * Minimal acceleration (inclusive) [m/s^2]
     */
    private final double       minAcceleration;
    /**
     * Max acceleration (inclusive) [m/s^^]
     */
    private final double       maxAcceleration;
    /**
     * Minimal velocity (inclusive) [m/s], 0 := agent can't reverse.
     */
    private final double       minVelocity;
    /**
     * Max velocity (inclusive) [m/s]
     */
    private final double       maxVelocity;
    /**
     * Length of the vehicle [m]
     */
    private final double       length;
    /**
     * Width of the vehicle [m]
     */
    private final double       width            = 1.7;
    /**
     * String representing the agent class (without suffix "Agent").
     */
    private final String       agent;

    private final static Shape SHAPE            = buildShape();

    public Vehicle() {
        this(-5, 5, 0, 33.3, 3, null);
    }

    public Vehicle(final double minAcceleration, final double maxAcceleration, final double minVelocity, final double maxVelocity, final double length, final String agent) {
        this.minAcceleration = minAcceleration;
        this.maxAcceleration = maxAcceleration;
        this.minVelocity = minVelocity;
        this.maxVelocity = maxVelocity;
        this.length = length;
        this.agent = agent;
    }

    public String getAgent() {
        return agent;
    }

    public double getLength() {
        return length;
    }

    public double getMaxAcceleration() {
        return maxAcceleration;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public double getMinAcceleration() {
        return minAcceleration;
    }

    public double getMinVelocity() {
        return minVelocity;
    }

    public Shape getShape() {
        return AffineTransform.getScaleInstance(getLength(), getWidth()).createTransformedShape(SHAPE);
    }

    public double getWidth() {
        return width;
    }
}
