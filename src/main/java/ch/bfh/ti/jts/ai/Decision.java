package ch.bfh.ti.jts.ai;

import java.io.Serializable;

import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Lane;

/**
 * Decision object. Contains all the decisions of an agent.
 *
 * @author Enteee
 * @author winki
 */
public class Decision implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private double     acceleration = 0.0;
    private LaneChange laneChange   = LaneChange.NONE;
    
    /**
     * Short-term decision. Can be null.
     */
    private Lane       turning      = null;
    /**
     * Long-term decision. Used with GPS. Can be null.
     */
    private Junction   destination  = null;
    
    public Decision() {
        laneChange = LaneChange.NONE;
    }
    
    public double getAcceleration() {
        return acceleration;
    }
    
    public LaneChange getLaneChangeDirection() {
        return laneChange;
    }
    
    public Lane getNextEdgeLane() {
        return turning;
    }
    
    public void setAcceleration(final double acceleration) {
        this.acceleration = acceleration;
    }
    
    public void setLaneChangeDirection(final LaneChange laneChangeDirection) {
        this.laneChange = laneChangeDirection;
    }
    
    public void setTurning(final Lane turning) {
        this.turning = turning;
    }
    
    public void setDestination(Junction destination) {
        this.destination = destination;
    }
    
    public Junction getDestination() {
        return destination;
    }
    
    @Override
    public String toString() {
        return String.format("Decision{ acceleration: %.2f, destination: %s, laneChange: %s, turning: %s }", acceleration, destination, laneChange, turning);
    }
}
