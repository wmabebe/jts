package ch.bfh.ti.jts.data;

public class Flow extends SpawnInfo {
    
    private static final long serialVersionUID = 1L;
    
    private double            frequency;
    private int               count;
    
    public Flow(Vehicle vehicle, Junction start, Junction end, double departureSpeed, double arrivalSpeed, double frequency) {
        super(vehicle, start, end, 0.0, 0.0, departureSpeed, 0.0, arrivalSpeed);
        this.frequency = frequency;
    }
    
    public Junction getRouteStart() {
        SpawnLocation start = getStart();
        if (start instanceof Junction) {
            return (Junction) start;
        }
        return null;
    }
    
    public Junction getRouteEnd() {
        SpawnLocation end = getEnd();
        if (end instanceof Junction) {
            return (Junction) end;
        }
        return null;
    }
    
    public double getFrequency() {
        return frequency;
    }
    
    public boolean isSpawn(double time) {
        assert time >= 0;
        
        if ((double) count / time < frequency) {
            count++;
            return true;
        }
        return false;
    }
}
