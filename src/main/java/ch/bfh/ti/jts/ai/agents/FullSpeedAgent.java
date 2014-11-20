package ch.bfh.ti.jts.ai.agents;

import ch.bfh.ti.jts.data.Vehicle;

public class FullSpeedAgent extends RandomAgent {
    
    private static final long serialVersionUID = 1L;
    
    public FullSpeedAgent(double positionOnLane, Vehicle vehicle, double velocity) {
        super(positionOnLane, vehicle, velocity);
    }
    
    @Override
    public void think() {
        super.think();
        getDecision().setAcceleration(getVehicle().getMaxAcceleration());
    }
}
