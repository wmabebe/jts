package ch.bfh.ti.jts.data;

import java.io.Serializable;

public class Route implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final Vehicle     vehicle;
    private final Edge        routeStart;
    private final Edge        routeEnd;
    private final double      departureTime;
    private final double      departurePos;
    private final double      departureSpeed;
    private final double      arrivalPos;
    private final double      arrivalSpeed;
    
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
