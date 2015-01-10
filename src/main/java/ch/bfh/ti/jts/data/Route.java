package ch.bfh.ti.jts.data;

/**
 * Route is a specialized SpawnInfo where an agent drive from A to B.
 *
 * @author Enteee
 * @author winki
 */
public class Route extends SpawnInfo {
    
    private static final long serialVersionUID = 1L;
    
    public Route(final Vehicle vehicle, final SpawnLocation routeStart, final SpawnLocation routeEnd, final double departureTime, final double departurePos, final double departureSpeed,
            final double arrivalPos, final double arrivalSpeed) {
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
