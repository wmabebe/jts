package ch.bfh.ti.jts.data;

public class Route extends SpawnInfo {
    
    private static final long serialVersionUID = 1L;
    
    public Route(Vehicle vehicle, Edge routeStart, Edge routeEnd, double departureTime, double departurePos, double departureSpeed, double arrivalPos, double arrivalSpeed) {
        super(vehicle, routeStart, routeEnd, departureTime, departurePos, departureSpeed, arrivalPos, arrivalSpeed);
    }
    
    public Edge getRouteStart() {
        SpawnLocation start = getStart();
        if (start instanceof Edge) {
            return (Edge) start;
        }
        return null;
    }
    
    public Edge getRouteEnd() {
        SpawnLocation end = getEnd();
        if (end instanceof Edge) {
            return (Edge) end;
        }
        return null;
    }
}
