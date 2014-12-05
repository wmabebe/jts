package ch.bfh.ti.jts.ai.agents;

import java.util.Collection;
import java.util.Random;

import ch.bfh.ti.jts.ai.Decision.LaneChangeDirection;
import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Vehicle;
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
    private final double      timeStep          = Simulation.SIMULATION_STEP_DURATION;
    /**
     * Distance the agent try to hold to the next agent next to him.
     */
    private final double      secureDistance    = 5.0;
    /**
     * Factor how patient an agent is. Value from 0 (no patience, wants to
     * overtake other agent as soon as possible) to 1 (never wants to overtake
     * other agents).
     */
    private final double      patienceFactor    = 0.3;
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
    private final double      niggleChance      = 0.3;
    /**
     * Factor how much the agent will niggle maximally. 0 means no slow down. 1
     * means slow maximally in the worst case.
     */
    private final double      niggleFactor      = 0.6;
    /**
     * Random object.
     */
    private final Random      rand;

    public RealisticAgent() {
        super();
        rand = new Random(getId());
    }

    private boolean canChangeLane(final Lane lane) {
        return getLaneChaneVelocity(lane) != -1;
    }

    private boolean doesNiggle() {
        return rand.nextDouble() < niggleChance;
    }

    private double getAccelerationToReachVelocity(final double goalVelocity) {
        return (goalVelocity - getVelocity()) / timeStep;
    }

    private double getLaneChaneVelocity(final Lane lane) {
        if (lane != null) {
            // minimal velocity if fully slow down
            final double minPossibleVelocity = getMinPossibleVelocityNextStep(this);
            // maximal velocity if fully speed up
            final double maxPossibleVelocity = getMaxPossibleVelocityNextStep(this);
            // make some tests with different velocities
            final int tests = 5; // must be at least 2
            for (int i = 0; i < tests; i++) {
                final double f = (double) i / (double) (tests - 1);
                final double velocityTest = minPossibleVelocity + f * (maxPossibleVelocity - minPossibleVelocity);
                // test for a specific distance on lane
                if (testLaneChange(lane, velocityTest)) {
                    // positive with velocity "velocityTest"
                    return velocityTest;
                }
            }
        }
        return -1; // no lane change possible
    }

    private double getMaxPossibleVelocityNextStep(final Agent agent) {
        final Vehicle vehicle = agent.getVehicle();
        final double maxPossibleVelocityWithoutLimit = agent.getVelocity() + timeStep * vehicle.getMaxAcceleration();
        return Helpers.clamp(maxPossibleVelocityWithoutLimit, vehicle.getMinVelocity(), vehicle.getMaxVelocity());
    }

    private double getMinPossibleVelocityNextStep(final Agent agent) {
        final Vehicle vehicle = agent.getVehicle();
        final double minPossibleVelocityWithoutLimit = agent.getVelocity() + timeStep * vehicle.getMinAcceleration();
        return Helpers.clamp(minPossibleVelocityWithoutLimit, vehicle.getMinVelocity(), vehicle.getMaxVelocity());
    }

    /**
     * Time the agent will wait, till it tries to overtake the other agent. For
     * patienceFactor 1, the agent will wait forever. For patienceFactor 0, the
     * agent will not wait. For everything between 0 and 1, the agent will after
     * the formula 300^patienceFactor.
     */
    private double getPatientTime() {
        assert patienceFactor >= 0.0;
        assert patienceFactor <= 1.0;

        if (patienceFactor == 0.0) {
            // agent will wait forever
            return Double.MAX_VALUE;
        }
        if (patienceFactor == 1.0) {
            // agent will not wait
            return 0;
        }
        return Math.pow(300, patienceFactor);
    }

    /**
     * Gets the secure velocity to not hit the next agent and to hold the secure
     * distance
     *
     * @param o
     *            other agent
     * @return secure velocity
     */
    private double getSecureVelocity(final Agent o) {
        assert o != null;

        // where is other agent in the specified amount of time if he decelerate
        // by the maximum?
        final double minVelocityOther = Helpers.clamp(o.getVelocity() + timeStep * o.getVehicle().getMinAcceleration(), o.getVehicle().getMinVelocity(), o.getVehicle().getMaxVelocity());
        // subtract the secure distance from the other agents position
        final double oPos = o.getPositionOnLane() + minVelocityOther * timeStep - secureDistance;
        if (oPos > getPositionOnLane()) {
            // calculate maximum possible speed
            return (oPos - getPositionOnLane()) / timeStep;
        }
        return getVehicle().getMinVelocity();
    }

    private boolean isImpatient() {
        final double waitTime = impatienceCounter * timeStep;
        final double patientTime = getPatientTime();
        return waitTime >= patientTime;
    }

    private double simulateMove(final double velocity) {
        final double distanceToDrive = velocity * timeStep;
        final double distanceOnLaneLeft = getAbsoluteDistanceOnLaneLeft();
        if (distanceOnLaneLeft >= distanceToDrive) {
            // stay on this lane
            return getPositionOnLane() + distanceToDrive;
        } else {
            // already on next lane
            // return max position (lane length)
            return getLane().getLength();
        }
    }

    private boolean testLaneChange(final Lane lane, final double velocity) {
        // where would the agent be?
        final double positionOnLane = simulateMove(velocity);
        // test all agents on the lane
        final Collection<Agent> agents = lane.getAgentsInOrder();
        for (final Agent agent : agents) {
            // minimal velocity if fully slow down
            final double minPossibleVelocity = getMinPossibleVelocityNextStep(agent);
            final double minPossiblePos = simulateMove(minPossibleVelocity);
            if (willProbablyCrash(this, positionOnLane, velocity, agent, minPossiblePos, minPossibleVelocity)) {
                // crash possible
                return false;
            }
            // maximal velocity if fully speed up
            final double maxPossibleVelocity = getMaxPossibleVelocityNextStep(agent);
            final double maxPossiblePos = simulateMove(maxPossibleVelocity);
            if (willProbablyCrash(this, positionOnLane, velocity, agent, maxPossiblePos, maxPossibleVelocity)) {
                // crash possible
                return false;
            }
        }
        // no possible crash detected
        return true;
    }

    @Override
    public void think() {
        super.think();

        assert timeStep > 0;

        // current properties of this agent
        final double maxPossibleVelocityNextStep = getMaxPossibleVelocityNextStep(this);

        // calculate secure velocity to not hit any of the agents in front of
        // this agent on the same lane
        double secureMaxVelocity = maxPossibleVelocityNextStep;
        for (final Agent o : getLane().getNextAgentsOnLine(this)) {
            final double secureVelocity = getSecureVelocity(o);
            secureMaxVelocity = Math.min(secureMaxVelocity, secureVelocity);
        }

        // does the agent has to slow down because of a slow agent in front of
        // him?
        final boolean hasToSlowDownBecauseOfOtherAgent = secureMaxVelocity < maxPossibleVelocityNextStep;
        final int impatienceDecrementFactor = 3;
        final int impatienceCounterMax = 100;
        if (hasToSlowDownBecauseOfOtherAgent) {
            impatienceCounter = Helpers.clamp(impatienceCounter + 1, 0, impatienceCounterMax);
        } else {
            impatienceCounter = Helpers.clamp(impatienceCounter - 1 * impatienceDecrementFactor, 0, impatienceCounterMax);
        }

        double targetAcceleration = getAccelerationToReachVelocity(secureMaxVelocity);

        // agent does only niggle when he is not impatient
        if (!isImpatient()) {
            if (doesNiggle()) {
                final double velocityRange = getVehicle().getMaxVelocity() - getVehicle().getMinVelocity();
                final double niggleVelocity = Helpers.clamp(getVehicle().getMinVelocity() + velocityRange * (1 - niggleFactor), getVehicle().getMinVelocity(), getVehicle().getMaxVelocity());
                // maximal speed is still the "secureMaxVelocity"
                if (niggleVelocity < secureMaxVelocity) {
                    // override target acceleration with the niggle acceleration
                    targetAcceleration = getAccelerationToReachVelocity(niggleVelocity);
                }
            }
        }

        // set max acceleration
        getDecision().setAcceleration(targetAcceleration);

        // check lane switching possibilities
        LaneChangeDirection direction = LaneChangeDirection.NONE;

        // does the agent want to overtake?
        final boolean isImpatient = isImpatient();
        if (isImpatient) {
            final Lane lane = getLane().getLeftLane().orElse(null);
            if (canChangeLane(lane)) {
                direction = LaneChangeDirection.LEFT;
            } else {
                direction = LaneChangeDirection.NONE;
            }
        } else {
            // try to go back on right lane
            final Lane lane = getLane().getRightLane().orElse(null);
            if (canChangeLane(lane)) {
                direction = LaneChangeDirection.RIGHT;
            } else {
                direction = LaneChangeDirection.NONE;
            }
        }
        getDecision().setLaneChangeDirection(direction);

        // TODO: use GPS
        // getDecision().setNextJunctionLane();
    }

    /**
     * Is there a chance that the two agents will crash?
     *
     * @param a
     *            agent A
     * @param aPos
     *            position of agent A
     * @param aV
     *            velocity of agent A
     * @param b
     *            agent B
     * @param bPos
     *            position of agent B
     * @param bV
     *            velocity of agent B
     * @return true, if there is a chance that the two agents will crash.
     */
    private boolean willProbablyCrash(final Agent a, double aPos, final double aV, final Agent b, double bPos, final double bV) {
        if (aPos > bPos) {
            // swap agents so that A is behind B.
            return willProbablyCrash(b, bPos, bV, a, aPos, aV);
        }
        // scenario: B slows down maximally, A speed up maximally
        bPos = bPos + timeStep * (bV + timeStep * b.getVehicle().getMinAcceleration());
        aPos = aPos + timeStep * (aV + timeStep * a.getVehicle().getMaxAcceleration());
        return aPos >= bPos; // crash?
    }
}
