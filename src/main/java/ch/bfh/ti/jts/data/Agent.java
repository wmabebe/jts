package ch.bfh.ti.jts.data;

import ch.bfh.ti.jts.ai.Brain;

public class Agent extends Element {
    
    private Brain brain;
    private Lane  lane;
    
    public Agent() {
    }
    
    public void setBrain(final Brain brain) {
        this.brain = brain;
    }
    
    public Brain getBrain() {
        return brain;
    }
    
    public void setLane(final Lane lane) {
        this.lane = lane;
    }
    
    public Lane getLane() {
        return lane;
    }
}
