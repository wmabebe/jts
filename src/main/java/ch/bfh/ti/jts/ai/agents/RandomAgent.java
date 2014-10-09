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
    
    private final static double MAX_ACCELERATION = Agent.MAX_ACCELERATION;
    private final static double MIN_ACCELERATION = Agent.MIN_ACCELERATION;
    
    @Override
    public void think(Decision decision) {
        decision.setAcceleration(ThreadLocalRandom.current().nextDouble() * (MAX_ACCELERATION - MIN_ACCELERATION) + MIN_ACCELERATION);
        decision.setLaneChangeDirection(Decision.LaneChangeDirection.randomLaneChange(ThreadLocalRandom.current()));
        Junction nextJunction = getLane().getEdge().getEnd();
        final List<Edge> nextEdges = new LinkedList<Edge>(nextJunction.getOutgoingEdges());
        // get all lanes from a random next edge
        final List<Lane> nextLanes = new LinkedList<Lane>(nextEdges.get(ThreadLocalRandom.current().nextInt(nextEdges.size())).getLanes());
        // select a random lane
        final Lane nextLane = nextLanes.get(ThreadLocalRandom.current().nextInt(nextLanes.size()));
        decision.setNextJunctionLane(nextLane);
    }
}