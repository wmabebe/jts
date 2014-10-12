package ch.bfh.ti.jts.ai.agents;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ch.bfh.ti.jts.ai.Decision;
import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Lane;

public class IdleAgent extends Agent {
    
    @Override
    public void think(Decision decision) {
        decision.setAcceleration(0);
        decision.setLaneChangeDirection(Decision.LaneChangeDirection.NONE);
        Junction nextJunction = getNextJunction();
        final List<Edge> nextEdges = new LinkedList<Edge>(nextJunction.getOutgoingEdges());
        // get all lanes from a random next edge
        final List<Lane> nextLanes = new LinkedList<Lane>(nextEdges.get(ThreadLocalRandom.current().nextInt(nextEdges.size())).getLanes());
        // select a random lane
        final Lane nextLane = nextLanes.get(ThreadLocalRandom.current().nextInt(nextLanes.size()));
        decision.setNextJunctionLane(nextLane);
    }
}
