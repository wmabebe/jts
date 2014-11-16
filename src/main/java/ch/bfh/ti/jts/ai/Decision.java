package ch.bfh.ti.jts.ai;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ch.bfh.ti.jts.data.Lane;

public class Decision implements Serializable {
    
    public static enum LaneChangeDirection {
        NONE, LEFT, RIGHT;
        
        public static LaneChangeDirection randomLaneChange(final Random random) {
            return VALUES.get(random.nextInt(VALUES.size()));
        }
        
        private static final List<LaneChangeDirection> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
        
    }
    private static final long   serialVersionUID    = 1L;
    private double              acceleration        = 0.0;
    private LaneChangeDirection laneChangeDirection = LaneChangeDirection.NONE;
    private Lane                nextJunctionLane    = null;
    
    public Decision() {
        laneChangeDirection = LaneChangeDirection.NONE;
    }
    
    @Override
    public String toString() {
        return "Decision a: " + acceleration + " laneChangeDirection: " + laneChangeDirection + " nextJunctionLane: " + nextJunctionLane;
    }
    
    public double getAcceleration() {
        return acceleration;
    }
    
    public LaneChangeDirection getLaneChangeDirection() {
        return laneChangeDirection;
    }
    
    public Lane getNextJunctionLane() {
        return nextJunctionLane;
    }
    
    public void setAcceleration(final double acceleration) {
        this.acceleration = acceleration;
    }
    
    public void setLaneChangeDirection(final LaneChangeDirection laneChangeDirection) {
        this.laneChangeDirection = laneChangeDirection;
    }
    
    public void setNextJunctionLane(final Lane nextJunctionLane) {
        this.nextJunctionLane = nextJunctionLane;
    }
}
