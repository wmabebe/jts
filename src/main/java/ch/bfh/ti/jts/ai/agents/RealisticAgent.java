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
        double nextDecisionTime = 1.0;
        
        // current properties of this agent
        Agent t = this;
        double tVelocity = getVelocity();
        double tAbsPosOnLane = getAbsPosOnLane();
        
        final Agent o = getLane().nextAgentOnLine(this);
        if (o != null) {
            // other agent in front of this agent on the same lane
            
            double oVelocity = o.getVelocity();
            double oAbsPosOnLane = o.getAbsPosOnLane();
            
            double maxVelocity = getVelocityToNotHitNextAgent(nextDecisionTime, t, o);
            
            // TODO: secure distance!
            
            // niggle?
            if (rand.nextDouble() < niggleChance) {
                Logger.getLogger(RealisticAgent.class.getName()).info("niggle...");
                Logger.getLogger(RealisticAgent.class.getName()).info("before: " + maxVelocity);
                maxVelocity = Helpers.clamp(maxVelocity * niggleFactor, 0.01, Double.MAX_VALUE);
                Logger.getLogger(RealisticAgent.class.getName()).info("after:" + maxVelocity);
            }
            
            double maxAcceleration = getAccelerationToReachVelocity(nextDecisionTime, tVelocity, maxVelocity);
            
            // set max acceleration
            getDecision().setAcceleration(maxAcceleration);
            Logger.getLogger(RealisticAgent.class.getName()).info("secure speed");
            
        } else {
            // no other agent front of this agent on the same lane
            // attention: next junction!
            getDecision().setAcceleration(getVehicle().getMaxAcceleration());
            Logger.getLogger(RealisticAgent.class.getName()).info("full speed");
        }
        
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
        
        // where is other agent in the specified amount of time?
        double oPos = o.getAbsPosOnLane() + o.getVelocity() * time;
        
        // calculate maximum possible speed
        return (oPos - t.getAbsPosOnLane()) / time;
    }
    
    private double getAccelerationToReachVelocity(double time, double currentVelocity, double goalVelocity) {
        if (time <= 0)
            throw new IllegalArgumentException("time is 0 or negative");
        
        return (goalVelocity - currentVelocity) / time;
    }
}
