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

    /**
     * Acceleration for the next simulation step.
     */
    private double            acceleration;
    /**
     * Decision to switch to another lane.
     */
    private LaneChange        laneChange;
    /**
     * Short-term decision. Can be null.
     */
    private Lane              turning;
    /**
     * Long-term decision. Used with GPS. Can be null.
     */
    private Junction          destination;

    public Decision() {
        acceleration = 0.0;
        laneChange = LaneChange.NONE;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public Junction getDestination() {
        return destination;
    }

    public LaneChange getLaneChange() {
        return laneChange;
    }

    public Lane getTurning() {
        return turning;
    }

    public void setAcceleration(final double acceleration) {
        this.acceleration = acceleration;
    }

    public void setDestination(final Junction destination) {
        this.destination = destination;
    }

    public void setLaneChange(final LaneChange laneChange) {
        this.laneChange = laneChange;
    }

    public void setTurning(final Lane turning) {
        this.turning = turning;
    }

    @Override
    public String toString() {
        return String.format("Decision{ acceleration: %.2f, destination: %s, laneChange: %s, turning: %s }", acceleration, destination, laneChange, turning);
    }
}
