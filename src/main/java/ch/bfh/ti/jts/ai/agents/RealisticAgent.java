package ch.bfh.ti.jts.ai.agents;

import java.util.Random;

import ch.bfh.ti.jts.ai.Decision.LaneChangeDirection;
import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.simulation.Simulation;
import ch.bfh.ti.jts.utils.Helpers;

/**
 * A agent which drives without collision.
 *
 * @author ente
 */
public class RealisticAgent extends RandomAgent {
    
    private static final long serialVersionUID  = 1L;
    
    /**
     * Time in seconds to the next decision.
     */
    private double            timeStep          = Simulation.SIMULATION_STEP_DURATION;
    /**
     * 
     */
    private final double      secureDistance    = 2.0;
    /**
     * Factor how patient an agent is. Value from 0 (no patience, wants to
     * overtake other agent as soon as possible) to 1 (never wants to overtake
     * other agents).
     */
    private final double      patienceFactor    = 0.5;
    /**
     * This counter is increased every simulation step when an agent has to slow
     * down because of another agent. It is decreased when the agent in not
     * hindered by another agent.
     */
    private int               impatienceCounter = 0;
    /**
     * Chance by which a agent will slow down from the maximum possible
     * velocity.
     */
    private final double      niggleChance      = 0.4;
    /**
     * Factor how much the agent will niggle maximally. 0 means no slow down. 1
     * means slow maximally in the worst case.
     */
    private final double      niggleFactor      = 0.4;
    private final Random      rand              = new Random();
    
    @Override
    public void think() {
        super.think();
        
        if (timeStep <= 0) {
            throw new IllegalArgumentException("time is 0 or negative");
        }
        
        // current properties of this agent
        double maxPossibleVelocityNextStep = getMaxPossibleVelocityNextStep();
        double minPossibleVelocityNextStep = getMinPossibleVelocityNextStep();
        
        // calculate secure velocity to not hit any of the agents in front of
        // this agent on the same lane
        double secureMaxVelocity = maxPossibleVelocityNextStep;
        for (final Agent o : getLane().getNextAgentsOnLine(this)) {
            double secureVelocity = getSecureVelocity(o);
            secureMaxVelocity = Math.min(secureMaxVelocity, secureVelocity);
        }
        
        // does the agent want to overtake?
        boolean tryToOvertake = isImpatient();
        
        // check lane switching possibilities
        boolean canGoLeft = canChangeLane(getLane().getLeftLane().orElse(null));
        boolean canGoRight = canChangeLane(getLane().getRightLane().orElse(null));;
        boolean canChangeLane = canGoLeft || canGoRight;
        
        // does the agent has to slow down because of a slow agent in front of
        // him?
        boolean hasToSlowDownBecauseOfOtherAgent = secureMaxVelocity < maxPossibleVelocityNextStep;
        if (hasToSlowDownBecauseOfOtherAgent) {
            impatienceCounter = Helpers.clamp(impatienceCounter + 1, 0, 100);
        } else {
            impatienceCounter = Helpers.clamp(impatienceCounter - 1, 0, 100);
        }
        
        double targetAcceleration = getAccelerationToReachVelocity(secureMaxVelocity);
        
        // does the agent niggle in this step
        boolean doesNiggle = (rand.nextDouble() < niggleChance);
        if (doesNiggle) {
            double velocityRange = getVehicle().getMaxVelocity() - getVehicle().getMinVelocity();
            double niggleVelocity = Helpers.clamp(getVehicle().getMinVelocity() + velocityRange * (1 - niggleFactor), getVehicle().getMinVelocity(), getVehicle().getMaxVelocity());
            
            if (niggleVelocity < secureMaxVelocity) {
                // override target acceleration with the niggle acceleration
                targetAcceleration = getAccelerationToReachVelocity(niggleVelocity);
            }
        }
        
        // set max acceleration
        getDecision().setAcceleration(targetAcceleration);
        
        // are there lanes left or right?
        LaneChangeDirection direction = LaneChangeDirection.NONE;
        if (canChangeLane) {
            // TODO: left right or straight
        }
        getDecision().setLaneChangeDirection(direction);
        
        // TODO: use GPS
        // getDecision().setNextJunctionLane();
    }
    
    // secure velocity to not hit the next agent and to hold the secure distance
    private double getSecureVelocity(Agent o) {
        if (o == null) {
            throw new IllegalArgumentException("agent is null");
        }
        // where is other agent in the specified amount of time if he decelerate
        // by the maximum?
        double minVelocityOther = Helpers.clamp(o.getVelocity() + timeStep * o.getVehicle().getMinAcceleration(), o.getVehicle().getMinVelocity(), o.getVehicle().getMaxVelocity());
        // subtract the secure distance from the other agents position
        double oPos = o.getAbsPosOnLane() + minVelocityOther * timeStep - secureDistance;
        if (oPos > getAbsPosOnLane()) {
            // calculate maximum possible speed
            return (oPos - getAbsPosOnLane()) / timeStep;
        }
        return getVehicle().getMinVelocity();
    }
    
    private double getAccelerationToReachVelocity(double goalVelocity) {
        return (goalVelocity - getVelocity()) / timeStep;
    }
    
    private double getMaxPossibleVelocityNextStep() {
        double maxPossibleVelocityWithoutLimit = getVelocity() + timeStep * getVehicle().getMaxAcceleration();
        return Helpers.clamp(maxPossibleVelocityWithoutLimit, getVehicle().getMinVelocity(), getVehicle().getMaxVelocity());
    }
    
    private double getMinPossibleVelocityNextStep() {
        double minPossibleVelocityWithoutLimit = getVelocity() + timeStep * getVehicle().getMinAcceleration();
        return Helpers.clamp(minPossibleVelocityWithoutLimit, getVehicle().getMinVelocity(), getVehicle().getMaxVelocity());
    }
    
    private double simulateMove(double velocity) {
        double distanceToDrive = velocity * timeStep;
        double distanceOnLaneLeft = getDistanceOnLaneLeft();
        if (distanceOnLaneLeft >= distanceToDrive) {
            // stay on this lane
            return getAbsPosOnLane() + distanceToDrive;
        } else {
            // already on next lane
            // return max position (lane length)
            return getLane().getLength();
        }
    }
    
    private boolean canChangeLane(Lane lane) {
        if (lane != null) {
            // minimal velocity if fully slow down
            double minPossiblePos = simulateMove(getMinPossibleVelocityNextStep());
            // maximal position if fully speed up
            double maxPossiblePos = simulateMove(getMaxPossibleVelocityNextStep());
            
            double secureSlitPos = findSecureSlitOnLane(lane, minPossiblePos, maxPossiblePos);
            if (secureSlitPos != -1) {
                return true;
            }
        }
        return false;
    }
    
    private double findSecureSlitOnLane(Lane lane, double minPossiblePos, double maxPossiblePos) {
        final int tests = 5; // must be at least 2
        for (int i = 0; i < tests; i++) {
            double f = (double) i / (double) (tests - 1);
            double absPosTest = (1 - f) * minPossiblePos + f * maxPossiblePos;
            double relPosTest = lane.getRelativePosition(absPosTest);
            
            // test
            if (testLaneChange(lane, relPosTest)) {
                return absPosTest;
            }
        }
        return -1; // case when no secure slit found
    }
    
    // TODO
    private boolean testLaneChange(Lane lane, double relativePosition) {
        
        // lane.getAgentsInOrder().stream().filter(x -> x.getRelativePosition()
        // < relativePosition);
        
        // lane.getNextAgentsOnLine(relativePosition);
        return false;
    }
    
    @SuppressWarnings("unused")
    private boolean isImpatient() {
        if (patienceFactor == 0.0) {
            return false;
        }
        if (patienceFactor == 1.0) {
            return true;
        }
        
        // TODO: reagrd of the wait time in the probability calculation
        double waitTime = impatienceCounter * timeStep;
        
        return (rand.nextDouble() < (1 - patienceFactor));
    }
}
