package ch.bfh.ti.jts.gui.data;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import ch.bfh.ti.jts.utils.Helpers;

public class PolyShape implements Serializable {

    private static List<Point2D> buildPoints(final String shapeString) {
        if (shapeString == null) {
            throw new IllegalArgumentException("shapeString is null");
        }
        if (!Pattern.matches(SHAPE_REGEX_STRING, shapeString)) {
            throw new IllegalArgumentException("shapeString has wrong format");
        }
        final List<Point2D> pointlist = new LinkedList<Point2D>();
        final String[] points = shapeString.split(" ");
        if (points.length == 0) {
            throw new IllegalArgumentException("shapeString has wrong format");
        }
        for (final String point : points) {
            final String[] coordinates = point.split(",");
            if (coordinates.length != 2) {
                throw new IllegalArgumentException("invalid coordinates");
            }
            final Point2D newPoint = new Point2D.Double(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]));
            pointlist.add(newPoint);
        }
        return pointlist;
    }
    private static final long   serialVersionUID   = 1L;
    private static final String SHAPE_REGEX_STRING = "^[-]?[0-9]+([.][0-9]+)[,][-]?[0-9]+([.][0-9]+)([ ][-]?[0-9]+([.][0-9]+)[,][-]?[0-9]+([.][0-9]+))*$";
    private final List<Point2D> points;
    private final Shape         shape;
    private final double        length;
    private final boolean       closedPath;
    private Point2D             position;

    private double              orientation;

    public PolyShape(final List<Point2D> points) {
        this(points, false);
    }

    public PolyShape(final List<Point2D> points, final boolean closedPath) {
        if (points == null) {
            throw new IllegalArgumentException("points is null");
        }
        if (points.size() == 0) {
            throw new IllegalArgumentException("points is empty");
        }
        this.closedPath = closedPath;
        this.points = points;
        shape = buildShape();
        length = buildLength();
    }

    public PolyShape(final String shapeString) {
        this(buildPoints(shapeString), false);
    }

    public PolyShape(final String shapeString, final boolean closedPath) {
        this(buildPoints(shapeString), closedPath);
    }

    private double buildLength() {
        double length = 0;
        if (points.size() > 1) {
            Point2D last = points.get(0);
            for (int i = 1; i < points.size(); i++) {
                final Point2D current = points.get(i);
                length += current.distance(last);
                last = current;
            }
        }
        return length;
    }

    private Shape buildShape() {
        final Path2D path = new Path2D.Double();
        for (int i = 0; i < points.size(); i++) {
            final Point2D point = points.get(i);
            if (i == 0) {
                path.moveTo(point.getX(), point.getY());
            } else {
                path.lineTo(point.getX(), point.getY());
            }
        }
        if (closedPath) {
            path.closePath();
        }
        return path;
    }

    private void calculate(double relative) {
        relative = Helpers.clamp(relative, 0, 1.0);
        if (points.size() == 2) {
            final double x = getStartPoint().getX() + relative * (getEndPoint().getX() - getStartPoint().getX());
            final double y = getStartPoint().getY() + relative * (getEndPoint().getY() - getStartPoint().getY());
            position = new Point2D.Double(x, y);
            orientation = getAngleBetweenTwoPoints(getStartPoint(), getEndPoint());
        }
        final double lengthOnPolyline = relative * length;
        followPolygon(lengthOnPolyline, 0);
    }

    private void followPolygon(final double distanceToFollow, int segment) {
        double distanceOnSegment;
        if (segment > points.size() - 2) {
            // if distance to follow is very small, we got an exception here
            // so this trick solves the problem
            segment = points.size() - 2; // last segment
            final double relativePositionOnSegment = 1.0;
            final Point2D segmentStart = points.get(segment);
            final Point2D segmentEnd = points.get(segment + 1);
            final double x = segmentStart.getX() + relativePositionOnSegment * (segmentEnd.getX() - segmentStart.getX());
            final double y = segmentStart.getY() + relativePositionOnSegment * (segmentEnd.getY() - segmentStart.getY());
            position = new Point2D.Double(x, y);
            orientation = getAngleBetweenTwoPoints(segmentStart, segmentEnd);
        }
        distanceOnSegment = getSegmentLength(segment);
        if (distanceToFollow <= distanceOnSegment) {
            final double relativePositionOnSegment = distanceToFollow / distanceOnSegment;
            final Point2D segmentStart = points.get(segment);
            final Point2D segmentEnd = points.get(segment + 1);
            final double x = segmentStart.getX() + relativePositionOnSegment * (segmentEnd.getX() - segmentStart.getX());
            final double y = segmentStart.getY() + relativePositionOnSegment * (segmentEnd.getY() - segmentStart.getY());
            position = new Point2D.Double(x, y);
            orientation = getAngleBetweenTwoPoints(segmentStart, segmentEnd);
        } else {
            // pass junction and switch to an other lane
            final double distanceToDriveOnNextSegment = distanceToFollow - distanceOnSegment;
            followPolygon(distanceToDriveOnNextSegment, segment + 1);
        }
    }

    private double getAngleBetweenTwoPoints(final Point2D p1, final Point2D p2) {
        final double dx = p2.getX() - p1.getX();
        final double dy = p2.getY() - p1.getY();
        return Math.atan2(dy, dx);
    }

    public Point2D getEndPoint() {
        return points.get(points.size() - 1);
    }

    public double getLength() {
        return length;
    }

    public double getRelativeOrientation(final double relative) {
        calculate(relative);
        return orientation;
    }

    public Point2D getRelativePosition(final double relative) {
        calculate(relative);
        return position;
    }

    private double getSegmentLength(final int index) {
        if (index < 0 || index > points.size() - 2) {
            throw new IndexOutOfBoundsException("index: " + index + " points.size: " + points.size());
        }
        return points.get(index).distance(points.get(index + 1));
    }

    public Shape getShape() {
        return shape;
    }

    public Point2D getStartPoint() {
        return points.get(0);
    }
}
