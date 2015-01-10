package ch.bfh.ti.jts.utils.graph;

import java.awt.Shape;
import java.awt.geom.Line2D;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.gui.PolyShape;

public class GPSTest {

    private final Shape     s     = new Line2D.Double(0, 0, 1, 1);
    private final PolyShape p     = new PolyShape("0.0,0.0 1.0,1.0");
    private final Net       net[] = new Net[10];
    private final Junction  j[]   = new Junction[100];
    private final Edge      e[]   = new Edge[100];

    @Before
    public void setUp() throws Exception {
        // @formatter:off
        /* Net0:
         *  j0 <-(e0,e1)-> j1
         *  | \             A
         *(e2) ---(e3)--\  (e4)
         *  v             v |
         *  j2-----(e5)---> j3
         *
         */
        // @formatter:on
        net[0] = new Net();
        j[0] = new Junction("j0", 0, 0, s);
        j[1] = new Junction("j1", 0, 0, s);
        j[2] = new Junction("j2", 0, 0, s);
        j[3] = new Junction("j3", 0, 0, s);
        net[0].addElement(j[0]);
        net[0].addElement(j[1]);
        net[0].addElement(j[2]);
        net[0].addElement(j[3]);
        e[0] = new Edge("e0", j[0], j[1], 1);
        new Lane("l", e[0], 0, 1, 1, p);
        e[1] = new Edge("e1", j[1], j[0], 1);
        new Lane("l", e[1], 0, 1, 1, p);
        e[2] = new Edge("e2", j[0], j[2], 1);
        new Lane("l", e[2], 0, 1, 1, p);
        e[3] = new Edge("e3", j[0], j[3], 1);
        new Lane("l", e[3], 0, 1, 1, p);
        e[4] = new Edge("e4", j[3], j[1], 1);
        new Lane("l", e[4], 0, 1, 1, p);
        e[5] = new Edge("e5", j[2], j[3], 1);
        new Lane("l", e[5], 0, 1, 1, p);
        net[0].addElement(e[0]);
        net[0].addElement(e[1]);
        net[0].addElement(e[2]);
        net[0].addElement(e[3]);
        net[0].addElement(e[4]);
        net[0].addElement(e[5]);
        // @formatter:off
        /* Net1:
         *  j4 -(e6)-> j5
         *  \          A
         * (e7)      (e8)
         *    \      /
         *     v    /
         *       j6
         *
         *  j7 <-(e9,e10)-> j8
         *
         * Whereas :
         *  e6.priority = 1
         *  e7.priority = 3
         *  e8.priority = 3
         */
        // @formatter:on
        net[1] = new Net();
        j[4] = new Junction("j4", 0, 0, s);
        j[5] = new Junction("j5", 0, 0, s);
        j[6] = new Junction("j6", 0, 0, s);
        j[7] = new Junction("j7", 0, 0, s);
        j[8] = new Junction("j8", 0, 0, s);
        net[1].addElement(j[4]);
        net[1].addElement(j[5]);
        net[1].addElement(j[6]);
        net[1].addElement(j[7]);
        net[1].addElement(j[8]);
        e[6] = new Edge("e6", j[4], j[5], 1);
        new Lane("l", e[6], 0, 1, 1, p);
        e[7] = new Edge("e7", j[4], j[6], 3);
        new Lane("l", e[7], 0, 1, 1, p);
        e[8] = new Edge("e8", j[6], j[5], 3);
        new Lane("l", e[8], 0, 1, 1, p);
        e[9] = new Edge("e9", j[7], j[8], 3);
        new Lane("l", e[9], 0, 1, 1, p);
        e[10] = new Edge("e10", j[8], j[7], 3);
        new Lane("l", e[10], 0, 1, 1, p);
        net[1].addElement(e[6]);
        net[1].addElement(e[7]);
        net[1].addElement(e[8]);
        net[1].addElement(e[9]);
        net[1].addElement(e[10]);
    }

    @Test
    public final void testGetNextEdge() {
        // NET 0
        final GPS<Junction, Edge> gps0 = new GPS<>(net[0]);
        // j0 outbound
        Assert.assertFalse(gps0.getNextEdge(j[0], j[0]).isPresent());
        Assert.assertEquals(e[0], gps0.getNextEdge(j[0], j[1]).get());
        Assert.assertEquals(e[2], gps0.getNextEdge(j[0], j[2]).get());
        Assert.assertEquals(e[3], gps0.getNextEdge(j[0], j[3]).get());
        // j1 outbound
        Assert.assertEquals(e[1], gps0.getNextEdge(j[1], j[0]).get());
        Assert.assertFalse(gps0.getNextEdge(j[1], j[1]).isPresent());
        Assert.assertEquals(e[1], gps0.getNextEdge(j[1], j[2]).get());
        Assert.assertEquals(e[1], gps0.getNextEdge(j[1], j[3]).get());
        // j2 outbound
        Assert.assertEquals(e[5], gps0.getNextEdge(j[2], j[0]).get());
        Assert.assertEquals(e[5], gps0.getNextEdge(j[2], j[1]).get());
        Assert.assertFalse(gps0.getNextEdge(j[2], j[2]).isPresent());
        Assert.assertEquals(e[5], gps0.getNextEdge(j[2], j[3]).get());
        // j3 outbound
        Assert.assertEquals(e[4], gps0.getNextEdge(j[3], j[0]).get());
        Assert.assertEquals(e[4], gps0.getNextEdge(j[3], j[1]).get());
        Assert.assertEquals(e[4], gps0.getNextEdge(j[3], j[2]).get());
        Assert.assertFalse(gps0.getNextEdge(j[3], j[3]).isPresent());
        // NET 1
        final GPS<Junction, Edge> gps1 = new GPS<>(net[1]);
        // j4 outbound
        Assert.assertFalse(gps1.getNextEdge(j[4], j[4]).isPresent());
        Assert.assertEquals(e[7], gps1.getNextEdge(j[4], j[5]).get()); // priority!
        Assert.assertEquals(e[7], gps1.getNextEdge(j[4], j[6]).get());
        Assert.assertFalse(gps1.getNextEdge(j[4], j[7]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[4], j[8]).isPresent());
        // j5 outbound
        Assert.assertFalse(gps1.getNextEdge(j[5], j[4]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[5], j[5]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[5], j[6]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[5], j[7]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[5], j[8]).isPresent());
        // j6 outbound
        Assert.assertFalse(gps1.getNextEdge(j[6], j[4]).isPresent());
        Assert.assertEquals(e[8], gps1.getNextEdge(j[6], j[5]).get());
        Assert.assertFalse(gps1.getNextEdge(j[6], j[6]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[6], j[7]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[6], j[8]).isPresent());
        // j7 outbound
        Assert.assertFalse(gps1.getNextEdge(j[7], j[4]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[7], j[5]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[7], j[6]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[7], j[7]).isPresent());
        Assert.assertEquals(e[9], gps1.getNextEdge(j[7], j[8]).get());
        // j8 outbound
        Assert.assertFalse(gps1.getNextEdge(j[8], j[4]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[8], j[5]).isPresent());
        Assert.assertFalse(gps1.getNextEdge(j[8], j[6]).isPresent());
        Assert.assertEquals(e[10], gps1.getNextEdge(j[8], j[7]).get());
        Assert.assertFalse(gps1.getNextEdge(j[8], j[8]).isPresent());
    }

    @Test
    public final void testGPS() {
        new GPS<>(net[0]);
        new GPS<>(net[1]);
    }

}
