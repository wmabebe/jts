package ch.bfh.ti.jts.ai.agents;

import ch.bfh.ti.jts.ai.Decision;

public class FullSpeedAgent extends RandomAgent {
    
    @Override
    public void think(Decision decision) {
        super.think(decision);
        decision.setAcceleration(getVehicle().getMaxAcceleration());
    }
}
