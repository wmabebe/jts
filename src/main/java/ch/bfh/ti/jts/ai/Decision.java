package ch.bfh.ti.jts.ai;

import ch.bfh.ti.jts.data.Lane;


public class Decision {
    
    public static enum LaneChangeDirection {
        None,
        Left,
        Right
    }
    
    private double acceleration;
    
    private LaneChangeDirection laneChangeDirection;
    
    private Lane nextJunctionLane;
}
