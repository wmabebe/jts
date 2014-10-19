package ch.bfh.ti.jts.data;

public class Route {
    
    private Vehicle vehicle;
    private Edge    routeStart;
    private Edge    routeEnd;
    private double  departureTime;
    private double  departurePos;
    private double  departureSpeed;
    private double  arrivalPos;
    private double  arrivalSpeed;
    
    public Route(final Vehicle vehicle, final Edge routeStart, final Edge routeEnd, final double departureTime, final double departurePos, final double departureSpeed, final double arrivalPos,
            final double arrivalSpeed) {
        this.vehicle = vehicle;
        this.routeStart = routeStart;
        this.routeEnd = routeEnd;
        this.departureTime = departureTime;
        this.departurePos = departurePos;
        this.departureSpeed = departureSpeed;
        this.arrivalPos = arrivalPos;
        this.arrivalSpeed = arrivalSpeed;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public Edge getRouteStart() {
        return routeStart;
    }
    
    public Edge getRouteEnd() {
        return routeEnd;
    }
    
    public double getDepartureTime() {
        return departureTime;
    }
    
    public double getDeparturePos() {
        return departurePos;
    }
    
    public double getDepartureSpeed() {
        return departureSpeed;
    }
    
    public double getArrivalPos() {
        return arrivalPos;
    }
    
    public double getArrivalSpeed() {
        return arrivalSpeed;
    }
}
