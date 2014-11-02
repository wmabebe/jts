package ch.bfh.ti.jts.utils.graph;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Net;

public class GPSTest {
    
    public static Net net = new Net();
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Junction j1 = new Junction("j", 0, 0, null);
        Junction j2 = new Junction("j", 0, 0, null);
        Junction j3 = new Junction("j", 0, 0, null);
        Edge e1 = new Edge("e", j1, j2, 0);
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public final void testGPS() {
        fail("Not yet implemented"); // TODO
    }
    
    @Test
    public final void testGetNextEdge() {
        fail("Not yet implemented"); // TODO
    }
    
    @Test
    public final void testUpdate() {
        fail("Not yet implemented"); // TODO
    }
    
}
