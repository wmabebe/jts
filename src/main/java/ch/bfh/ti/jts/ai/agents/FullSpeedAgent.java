package ch.bfh.ti.jts.ai.agents;

/**
 * Agent which accelerates always by its maximal value. His goal is to reach
 * full speed as soon as possible.
 * 
 * @author Enteee
 * @author winki
 */
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
