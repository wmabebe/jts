package ch.bfh.ti.jts.data;

import ch.bfh.ti.jts.ai.Brain;

public class Agent extends Element {
    
    public final static int AGENT_LAYER = Junction.JUNCTION_LAYER + 1;
    private Brain           brain;
    private Lane            lane;
    
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
    
    @Override
    public int getLayer() {
        return AGENT_LAYER;
    }
}
