package ch.bfh.ti.jts.ai.agents;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import ch.bfh.ti.jts.ai.LaneChange;
import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Vehicle;

/**
 * Agent which decides always randomly.
 * 
 * @author Enteee
 * @author winki
 */
public class RandomAgent extends Agent {
    
    private static final long serialVersionUID = 1L;
    
    public RandomAgent() {
        super();
    }
    
    @Override
    public void think() {
        getDecision().setAcceleration(getRandomAcceleration());
        getDecision().setLaneChange(getRandomLaneChange());
        final Junction nextJunction = getLane().getEdge().getEnd();
        final List<Edge> nextEdges = new LinkedList<Edge>(nextJunction.getOutgoingEdges());
        if (nextEdges.size() > 0) {
            // get all lanes from a random next edge
            final List<Lane> nextLanes = new LinkedList<Lane>(nextEdges.get(ThreadLocalRandom.current().nextInt(nextEdges.size())).getLanes());
            // select a random lane
            final Lane nextLane = nextLanes.get(ThreadLocalRandom.current().nextInt(nextLanes.size()));
            getDecision().setTurning(nextLane);
        }
    }
    
    private double getRandomAcceleration() {
        Vehicle vehicle = getVehicle();
        return ThreadLocalRandom.current().nextDouble() * (vehicle.getMaxAcceleration() - vehicle.getMinAcceleration()) + vehicle.getMinAcceleration();
    }
    
    private LaneChange getRandomLaneChange() {
        final List<LaneChange> values = Collections.unmodifiableList(Arrays.asList(LaneChange.values()));
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }
}
