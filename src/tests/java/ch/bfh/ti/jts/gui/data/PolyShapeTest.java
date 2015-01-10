package ch.bfh.ti.jts.gui.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ch.bfh.ti.jts.gui.PolyShape;

public class PolyShapeTest {

    @Test
    public void constructor() {
        // length: 363.83
        final String shapeString = "558.31,104.01 473.01,382.50 469.86,446.24";
        final List<Point2D> points = new LinkedList<Point2D>();
        points.add(new Point2D.Double(558.31, 104.01));
        points.add(new Point2D.Double(473.01, 382.50));
        points.add(new Point2D.Double(469.86, 446.24));
        PolyShape ps = new PolyShape(shapeString);
        assertEquals(ps.getStartPoint(), points.get(0));
        assertEquals(ps.getEndPoint(), points.get(2));
        ps = new PolyShape(points);
        assertEquals(ps.getStartPoint(), points.get(0));
        assertEquals(ps.getEndPoint(), points.get(2));
    }

    @Test
    public void getLengthMorePoints() {
        final List<Point2D> points = new LinkedList<Point2D>();
        points.add(new Point2D.Double(0.0, 0.0));
        points.add(new Point2D.Double(0.0, 10.0));
        points.add(new Point2D.Double(10.0, 10.0));
        final PolyShape ps = new PolyShape(points);
        assertTrue(ps.getLength() == 20.0);
    }

    @Test
    public void getLengthTwoPoints() {
        final List<Point2D> points = new LinkedList<Point2D>();
        points.add(new Point2D.Double(0.0, 0.0));
        points.add(new Point2D.Double(0.0, 10.0));
        final PolyShape ps = new PolyShape(points);
        assertTrue(ps.getLength() == 10.0);
    }

    @Test
    public void getOrientation() {
        // angle: 0
        List<Point2D> points = new LinkedList<Point2D>();
        points.add(new Point2D.Double(0.0, 0.0));
        points.add(new Point2D.Double(10.0, 0.0));
        PolyShape ps = new PolyShape(points);
        assertTrue(ps.getRelativeOrientation(0.5) == 0.0);
        // angle: PI / 4
        points = new LinkedList<Point2D>();
        points.add(new Point2D.Double(0.0, 0.0));
        points.add(new Point2D.Double(10.0, 10.0));
        ps = new PolyShape(points);
        assertTrue(ps.getRelativeOrientation(0.5) == Math.PI / 4);
        // angle: PI / 2
        points = new LinkedList<Point2D>();
        points.add(new Point2D.Double(0.0, 0.0));
        points.add(new Point2D.Double(0.0, 10.0));
        ps = new PolyShape(points);
        assertTrue(ps.getRelativeOrientation(0.5) == Math.PI / 2);
        // angle: PI
        points = new LinkedList<Point2D>();
        points.add(new Point2D.Double(0.0, 0.0));
        points.add(new Point2D.Double(-10.0, 0.0));
        ps = new PolyShape(points);
        assertTrue(ps.getRelativeOrientation(0.5) == Math.PI);
        // angle: - PI / 2
        points = new LinkedList<Point2D>();
        points.add(new Point2D.Double(0.0, 0.0));
        points.add(new Point2D.Double(0.0, -10.0));
        ps = new PolyShape(points);
        assertTrue(ps.getRelativeOrientation(0.5) == -Math.PI / 2);
    }

    @Test
    public void getRelativePosition() {
        final List<Point2D> points = new LinkedList<Point2D>();
        points.add(new Point2D.Double(0.0, 0.0));
        points.add(new Point2D.Double(0.0, 10.0));
        final PolyShape ps = new PolyShape(points);
        assertEquals(ps.getRelativePosition(0.5), new Point2D.Double(0, 5.0));
    }
}
