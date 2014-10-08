package ch.bfh.ti.jts.ai;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.bfh.ti.jts.data.Lane;

public class Decision {
    
    public static enum LaneChangeDirection {
        NONE, LEFT, RIGHT;
        
        private static final List<LaneChangeDirection> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
        private static final int                       SIZE   = VALUES.size();
        
        public static LaneChangeDirection randomLaneChange(final Random random) {
            return VALUES.get(random.nextInt(VALUES.size()));
        }
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
