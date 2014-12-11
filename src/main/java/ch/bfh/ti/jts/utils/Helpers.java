package ch.bfh.ti.jts.utils;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Helpers {
    
    /**
     * Make sure a value is in between the specified bounds.
     *
     * @param value
     *            the value
     * @param min
     *            the minimal possible value (inclusive)
     * @param max
     *            the maximal possible value (inclusive)
     * @return the clamped value
     */
    public static double clamp(double value, final double min, final double max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }
    
    /**
     * @see Helpers#clamp(double, double, double)
     */
    public static int clamp(int value, final int min, final int max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }
    
    /**
     * Point in the middle between two points.
     * 
     * @param a
     *            first point
     * @param b
     *            second point
     * @return point in the middle
     */
    public static Point2D pointBetween(Point2D a, Point2D b) {
        double x = a.getX() + 0.5 * (b.getX() - a.getX());
        double y = a.getY() + 0.5 * (b.getY() - a.getY());
        return new Point2D.Double(x, y);
    }
    
    /**
     * Given a line based on two points, and a point away from the line, find
     * the perpendicular distance from the point to the line. see
     * http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html for
     * explanation and defination. Source:
     * 
     * @see http 
     *      ://www.java2s.com/Code/CSharp/Development-Class/DistanceFromPointToLine
     *      .htm
     * @param point
     * @param line
     * @return
     */
    public static double distancePointToLine(Point2D point, Line2D line) {
        Point2D l1 = line.getP1();
        Point2D l2 = line.getP2();
        return Math.abs((l2.getX() - l1.getX()) * (l1.getY() - point.getY()) - (l1.getX() - point.getX()) * (l2.getY() - l1.getY()))
                / Math.sqrt(Math.pow(l2.getX() - l1.getX(), 2) + Math.pow(l2.getY() - l1.getY(), 2));
    }
}
