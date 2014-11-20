package ch.bfh.ti.jts.ai.agents;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.data.Vehicle;

public class IdleAgent extends RandomAgent {
    
    public IdleAgent(double positionOnLane, Vehicle vehicle, double velocity) {
        super(positionOnLane, vehicle, velocity);
    }
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public void think() {
        super.think();
        getDecision().setAcceleration(0);
        getDecision().setLaneChangeDirection(Decision.LaneChangeDirection.NONE);
    }
}
