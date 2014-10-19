package ch.bfh.ti.jts.ai.agents;

import ch.bfh.ti.jts.ai.Decision;

public class IdleAgent extends RandomAgent {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public void think(Decision decision) {
        super.think(decision);
        decision.setAcceleration(0);
        decision.setLaneChangeDirection(Decision.LaneChangeDirection.NONE);
    }
}
