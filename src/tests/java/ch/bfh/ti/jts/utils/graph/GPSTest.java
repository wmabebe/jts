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

public class GPSTest {
    
    private final Shape    s     = new Line2D.Double(0, 0, 1, 1);
    private final Lane     l     = new Lane("l", null, 0, 1, 1, s);
    private final Net      net[] = new Net[10];
    private final Junction j[]   = new Junction[100];
    private final Edge     e[]   = new Edge[100];
    
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
        e[1] = new Edge("e1", j[1], j[0], 1);
        e[2] = new Edge("e2", j[0], j[2], 1);
        e[3] = new Edge("e3", j[0], j[3], 1);
        e[4] = new Edge("e4", j[3], j[1], 1);
        e[5] = new Edge("e5", j[2], j[3], 1);
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
         */
        // @formatter:on
        net[1] = new Net();
        j[4] = new Junction("j4", 0, 0, s);
        j[5] = new Junction("j5", 0, 0, s);
        j[6] = new Junction("j6", 0, 0, s);
        e[6] = new Edge("e6", j[4], j[5], 1);
        e[7] = new Edge("e7", j[4], j[6], 3);
        e[8] = new Edge("e8", j[6], j[5], 3);
    }
    
    @Test
    public final void testGPS() {
        new GPS<>(net[0]);
        new GPS<>(net[1]);
    }
    
    @Test
    public final void testGetNextEdge() {
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
        final GPS<Junction, Edge> gps1 = new GPS<>(net[0]);
        // j0 outbound
        Assert.assertEquals(null, gps1.getNextEdge(j[0], j[0]));
        Assert.assertEquals(e[0], gps1.getNextEdge(j[0], j[1]));
        Assert.assertEquals(e[2], gps1.getNextEdge(j[0], j[2]));
        Assert.assertEquals(e[3], gps1.getNextEdge(j[0], j[3]));
        // j1 outbound
        Assert.assertEquals(e[1], gps1.getNextEdge(j[1], j[0]));
        Assert.assertEquals(null, gps1.getNextEdge(j[1], j[1]));
        Assert.assertEquals(e[1], gps1.getNextEdge(j[1], j[2]));
        Assert.assertEquals(e[1], gps1.getNextEdge(j[1], j[3]));
        // j2 outbound
        Assert.assertEquals(e[5], gps1.getNextEdge(j[2], j[0]));
        Assert.assertEquals(e[5], gps1.getNextEdge(j[2], j[1]));
        Assert.assertEquals(null, gps1.getNextEdge(j[2], j[2]));
        Assert.assertEquals(e[5], gps1.getNextEdge(j[2], j[3]));
        // j3 outbound
        Assert.assertEquals(e[4], gps1.getNextEdge(j[3], j[0]));
        Assert.assertEquals(e[4], gps1.getNextEdge(j[3], j[1]));
        Assert.assertEquals(e[4], gps1.getNextEdge(j[3], j[2]));
        Assert.assertEquals(null, gps1.getNextEdge(j[3], j[3]));
        
    }
    
    @Test
    public final void testUpdate() {
        
    }
    
}
