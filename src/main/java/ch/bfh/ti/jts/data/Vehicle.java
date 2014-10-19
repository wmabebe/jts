package ch.bfh.ti.jts.data;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class Vehicle {
    
    /**
     * Minimal acceleration (inclusive) [m/s^2]
     */
    private double             minAcceleration;
    /**
     * Max acceleration (inclusive) [m/s^^]
     */
    private double             maxAcceleration;
    /**
     * Minimal velocity (inclusive) [m/s], 0 := agent can't reverse.
     */
    private double             minVelocity;
    /**
     * Max velocity (inclusive) [m/s]
     */
    private double             maxVelocity;
    /**
     * The vehicles length [m]
     */
    private double             length;
    /**
     * The vehicles width [m]
     */
    private final double       width = 1.7;
    private final static Shape SHAPE = buildShape();
    
    public Vehicle() {
        this(-5, 5, 0, 33.3, 3);
    }
    
    public Vehicle(final double minAcceleration, final double maxAcceleration, final double minVelocity, final double maxVelocity, final double length) {
        this.minAcceleration = minAcceleration;
        this.maxAcceleration = maxAcceleration;
        this.minVelocity = minVelocity;
        this.maxVelocity = maxVelocity;
        this.length = length;
    }
    
    public Shape getShape() {
        return AffineTransform.getScaleInstance(getLength(), getWidth()).createTransformedShape(SHAPE);
    }
    
    public double getMaxAcceleration() {
        return maxAcceleration;
    }
    
    public double getMinAcceleration() {
        return minAcceleration;
    }
    
    public double getMaxVelocity() {
        return maxVelocity;
    }
    
    public double getMinVelocity() {
        return minVelocity;
    }
    
    public double getLength() {
        return length;
    }
    
    public double getWidth() {
        return width;
    }
    
    private static final Shape buildShape() {
        final Path2D path = new Path2D.Double();
        path.moveTo(0.5, 0.0);
        path.lineTo(-0.5, 0.5);
        path.lineTo(-0.5, -0.5);
        path.closePath();
        return path;
    }
}
