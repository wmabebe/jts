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
        this.laneChangeDirection = LaneChangeDirection.NONE;
    }
    
    public double getAcceleration() {
        return acceleration;
    }
    
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }
    
    public LaneChangeDirection getLaneChangeDirection() {
        return laneChangeDirection;
    }
    
    public void setLaneChangeDirection(LaneChangeDirection laneChangeDirection) {
        this.laneChangeDirection = laneChangeDirection;
    }
    
    public Lane getNextJunctionLane() {
        return nextJunctionLane;
    }
    
    public void setNextJunctionLane(Lane nextJunctionLane) {
        this.nextJunctionLane = nextJunctionLane;
    }
}
