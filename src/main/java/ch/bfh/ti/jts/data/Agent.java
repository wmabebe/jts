package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import ch.bfh.ti.jts.ai.Brain;
import ch.bfh.ti.jts.gui.App;

public class Agent extends Element {
    
    public final static int AGENT_LAYER = Junction.JUNCTION_LAYER + 1;
    private Brain           brain;
    private Lane            lane;
    /**
     * The relative position of the agent on the lane
     */
    private double          position    = 0;
    /**
     * The velocity of an agent
     */
    private double          velocity    = 14;
    
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
    
    public void setPosition(final Double position) {
        this.position = position;
    }
    
    public double getPosition() {
        return position;
    }
    
    public void setVelocity(final Double velocity) {
        this.velocity = velocity;
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    @Override
    public int getLayer() {
        return AGENT_LAYER;
    }
    
    @Override
    public void render(Graphics2D g) {
        if (App.DEBUG) {
            g.setStroke(new BasicStroke(1));
            g.setColor(Color.RED);
        }
    }
    
    @Override
    public void simulate(Element oldSelf, Duration duration) {
        // set new position
        setPosition(getPosition() + getVelocity() * duration.get(ChronoUnit.SECONDS));
    }
}
