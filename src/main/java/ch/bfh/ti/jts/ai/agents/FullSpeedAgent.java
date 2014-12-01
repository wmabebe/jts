package ch.bfh.ti.jts.ai.agents;

public class FullSpeedAgent extends RandomAgent {
    
    private static final long serialVersionUID = 1L;
    
    public FullSpeedAgent() {
        super();
    }
        
    @Override
    public void think() {
        super.think();
        getDecision().setAcceleration(getVehicle().getMaxAcceleration());
    }
}
