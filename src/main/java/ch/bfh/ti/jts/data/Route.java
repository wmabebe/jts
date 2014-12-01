package ch.bfh.ti.jts.data;

public class Route extends SpawnInfo {

    private static final long serialVersionUID = 1L;

    public Route(final Vehicle vehicle, final Edge routeStart, final Edge routeEnd, final double departureTime, final double departurePos, final double departureSpeed, final double arrivalPos,
            final double arrivalSpeed) {
        super(vehicle, routeStart, routeEnd, departureTime, departurePos, departureSpeed, arrivalPos, arrivalSpeed);
    }

    public Edge getRouteEnd() {
        final SpawnLocation end = getEnd();
        if (end instanceof Edge) {
            return (Edge) end;
        }
        return null;
    }

    public Edge getRouteStart() {
        final SpawnLocation start = getStart();
        if (start instanceof Edge) {
            return (Edge) start;
        }
        return null;
    }
}
