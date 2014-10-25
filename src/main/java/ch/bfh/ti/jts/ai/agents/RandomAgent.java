package ch.bfh.ti.jts.ai.agents;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Lane;

/**
 * A agent which does random stuff.
 * 
 * @author ente
 */
public class RandomAgent extends Agent {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public void think() {
        getDecision().setAcceleration(ThreadLocalRandom.current().nextDouble() * (getVehicle().getMaxAcceleration() - getVehicle().getMinAcceleration()) + getVehicle().getMinAcceleration());
        getDecision().setLaneChangeDirection(Decision.LaneChangeDirection.randomLaneChange(ThreadLocalRandom.current()));
        Junction nextJunction = getLane().getEdge().getEnd();
        final List<Edge> nextEdges = new LinkedList<Edge>(nextJunction.getOutgoingEdges());
        if (nextEdges.size() == 0) {
            // throw new RuntimeException("error: no next edges");
        } else {
            // get all lanes from a random next edge
            final List<Lane> nextLanes = new LinkedList<Lane>(nextEdges.get(ThreadLocalRandom.current().nextInt(nextEdges.size())).getLanes());
            // select a random lane
            final Lane nextLane = nextLanes.get(ThreadLocalRandom.current().nextInt(nextLanes.size()));
            getDecision().setNextJunctionLane(nextLane);
        }
    }
}
