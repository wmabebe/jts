package ch.bfh.ti.jts.ai.agents;

import ch.bfh.ti.jts.data.Agent;

/**
 * A agent which drives without collision.
 *
 * @author ente
 */
public class PerfectAgent extends Agent {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public void think() {
        final Agent nextAgentOnLane = getLane().getAgents().ceiling(this);
        final double distanceOnLaneLeft = getDistanceOnLaneLeft();
        // TODO: see below
        // getDecision().setAcceleration();
        // getDecision().setLaneChangeDirection();
        // getDecision().setNextJunctionLane();
    }
}
