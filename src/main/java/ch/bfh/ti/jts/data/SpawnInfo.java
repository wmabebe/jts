package ch.bfh.ti.jts.data;

import java.io.Serializable;

/**
 * SpawnInfo manages the spawning of agents.
 *
 * @author Enteee
 * @author winki
 */
public class SpawnInfo implements Serializable {

    private static final long   serialVersionUID = 1L;

    private final Vehicle       vehicle;
    private final SpawnLocation start;
    private final SpawnLocation end;
    private final double        departureTime;
    private final double        departurePos;
    private final double        departureSpeed;
    private final double        arrivalPos;
    private final double        arrivalSpeed;

    public SpawnInfo(final Vehicle vehicle, final SpawnLocation start, final SpawnLocation end, final double departureTime, final double departurePos, final double departureSpeed,
            final double arrivalPos, final double arrivalSpeed) {
        this.vehicle = vehicle;
        this.start = start;
        this.end = end;
        this.departureTime = departureTime;
        this.departurePos = departurePos;
        this.departureSpeed = departureSpeed;
        this.arrivalPos = arrivalPos;
        this.arrivalSpeed = arrivalSpeed;
    }

    public double getArrivalPos() {
        return arrivalPos;
    }

    public double getArrivalSpeed() {
        return arrivalSpeed;
    }

    public double getDeparturePos() {
        return departurePos;
    }

    public double getDepartureSpeed() {
        return departureSpeed;
    }

    public double getDepartureTime() {
        return departureTime;
    }

    public SpawnLocation getEnd() {
        return end;
    }

    public Junction getEndJunction() {
        if (end instanceof Junction) {
            return (Junction) end;
        }
        if (end instanceof Edge) {
            final Edge edge = (Edge) end;
            return edge.getEnd();
        }
        if (end instanceof Lane) {
            final Lane lane = (Lane) end;
            final Edge edge = lane.getEdge();
            return edge.getEnd();
        }
        return null;
    }

    public SpawnLocation getStart() {
        return start;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}
