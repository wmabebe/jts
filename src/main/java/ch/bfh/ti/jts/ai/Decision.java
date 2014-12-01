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
    private Lane                nextEdgeLane        = null;

    public Decision() {
        laneChangeDirection = LaneChangeDirection.NONE;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public LaneChangeDirection getLaneChangeDirection() {
        return laneChangeDirection;
    }

    public Lane getNextEdgeLane() {
        return nextEdgeLane;
    }

    public void setAcceleration(final double acceleration) {
        this.acceleration = acceleration;
    }

    public void setLaneChangeDirection(final LaneChangeDirection laneChangeDirection) {
        this.laneChangeDirection = laneChangeDirection;
    }

    public void setNextEdgeLane(final Lane nextEdgeLane) {
        this.nextEdgeLane = nextEdgeLane;
    }

    @Override
    public String toString() {
        return "Decision a: " + acceleration + " laneChangeDirection: " + laneChangeDirection + " nextEdgeLane: " + nextEdgeLane;
    }

}
