package ch.bfh.ti.jts.ai;

import ch.bfh.ti.jts.data.Lane;

public class Decision {
    
    public static enum LaneChangeDirection {
        NONE, LEFT, RIGHT
    }
    private double              acceleration;
    private LaneChangeDirection laneChangeDirection;
    private Lane                nextJunctionLane;
    
    public Decision() {
        laneChangeDirection = LaneChangeDirection.NONE;
    }
    
    public double getAcceleration() {
        return acceleration;
    }
    
    public void setAcceleration(final double acceleration) {
        this.acceleration = acceleration;
    }
    
    public LaneChangeDirection getLaneChangeDirection() {
        return laneChangeDirection;
    }
    
    public void setLaneChangeDirection(final LaneChangeDirection laneChangeDirection) {
        this.laneChangeDirection = laneChangeDirection;
    }
    
    public Lane getNextJunctionLane() {
        return nextJunctionLane;
    }
    
    public void setNextJunctionLane(final Lane nextJunctionLane) {
        this.nextJunctionLane = nextJunctionLane;
    }
}
