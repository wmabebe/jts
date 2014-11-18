package ch.bfh.ti.jts.ai.agents;

import java.util.Random;
import java.util.logging.Logger;

import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.utils.Helpers;

/**
 * A agent which drives without collision.
 *
 * @author ente
 */
public class RealisticAgent extends Agent {
    
    private static final long serialVersionUID = 1L;
    
    private final double      niggleChance     = 0.5;
    private final double      niggleFactor     = 0.1;
    private final Random      rand             = new Random();
    
    @Override
    public void think() {
        
        // time in seconds to the next decision
        double nextDecisionTime = 0.25;
        
        // current properties of this agent
        double tVelocity = getVelocity();
        double tAbsPosOnLane = getAbsPosOnLane();
        
        double minMaxVelocity = Double.MAX_VALUE;
        
        if (getLane() == null) {
            throw new RuntimeException("no lane!");
        }
        
        for (final Agent o : getLane().nextAgentsOnLine(this)) {
            // other agent in front of this agent on the same lane
            minMaxVelocity = Math.min(minMaxVelocity, getVelocityToNotHitNextAgent(nextDecisionTime, this, o));
        }
        
        // niggle?
        if (rand.nextDouble() < niggleChance) {
            minMaxVelocity = Helpers.clamp(minMaxVelocity * niggleFactor, 0.001, Double.MAX_VALUE);
        }
        
        double maxAcceleration = getAccelerationToReachVelocity(nextDecisionTime, tVelocity, minMaxVelocity);
        
        // set max acceleration
        getDecision().setAcceleration(maxAcceleration);
        // Logger.getLogger(RealisticAgent.class.getName()).info("secure speed");
        
        final double distanceOnLaneLeft = getDistanceOnLaneLeft();
        
        // acceleration
        
        // getDecision().setAcceleration();
        
        // lane change direction
        
        // getDecision().setLaneChangeDirection();
        
        // turn
        
        // getDecision().setNextJunctionLane();
        
        // TODO: see below
        // getDecision().setLaneChangeDirection();
    }
    
    private double getVelocityToNotHitNextAgent(double time, Agent t, Agent o) {
        if (time <= 0)
            throw new IllegalArgumentException("time is 0 or negative");
        if (t == null)
            throw new IllegalArgumentException("agent t is null");
        if (o == null)
            throw new IllegalArgumentException("agent o is null");
        
        // where is other agent in the specified amount of time if he decelerate
        // the maximum?
        double pMinVelocity = o.getVelocity() + time * o.getVehicle().getMinAcceleration();
        double oPos = o.getAbsPosOnLane() + pMinVelocity * time;
        
        // calculate maximum possible speed
        return (oPos - t.getAbsPosOnLane()) / time;
    }
    
    private double getAccelerationToReachVelocity(double time, double currentVelocity, double goalVelocity) {
        if (time <= 0)
            throw new IllegalArgumentException("time is 0 or negative");
        
        return (goalVelocity - currentVelocity) / time;
    }
}
