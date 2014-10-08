package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.bfh.ti.jts.ai.Brain;
import ch.bfh.ti.jts.gui.App;
import ch.bfh.ti.jts.simulation.Simulatable;

public class Agent extends Element {
    
    public final static int     AGENT_LAYER = Junction.JUNCTION_LAYER + 1;
    private final static double size        = 5.0;
    private final static Shape  shape       = new Ellipse2D.Double(-size / 2, -size / 2, size, size);
    private Brain               brain;
    private Lane                lane;
    /**
     * The relative position of the agent on the lane (>= 0.0 and < 1.0)
     */
    private double              position    = 0;
    /**
     * The velocity of an agent in m/s
     */
    private double              velocity    = 14;
    
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
        lane.getAgents().add(this);
    }
    
    public Lane getLane() {
        return lane;
    }
    
    public void setPosition(final Double position) {
        if (position < 0 || position > 1.0)
            throw new IllegalArgumentException("position");
        this.position = position;
    }
    
    public double getPosition() {
        return position;
    }
    
    private double getX() {
        Junction start = getLane().getEdge().getStart();
        Junction end = getLane().getEdge().getEnd();
        return getPosition() * (end.getX() - start.getX());
    }
    
    private double getY() {
        Junction start = getLane().getEdge().getStart();
        Junction end = getLane().getEdge().getEnd();
        return getPosition() * (end.getY() - start.getY());
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
        double x = getX();
        double y = getY();
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.RED);
        g.translate(x, y);
        g.fill(shape);
        g.translate(-x, -y);
    }
    
    @Override
    public void simulate(Duration duration) {
        double distanceToDrive = getVelocity() * duration.getNano() * 10E-9;
        followLane(getLane(), distanceToDrive);
    }
    
    private void followLane(final Lane currentLane, final double distanceToDrive) {
        double lengthLane = currentLane.getLength();
        double distanceOnLaneLeft = lengthLane * (1 - getPosition());
        if (distanceToDrive <= distanceOnLaneLeft) {
            // stay on this edge
            setPosition(getPosition() + distanceToDrive / lengthLane);
        } else {
            // pass junction and switch to an other lane
            double distanceToDriveOnNewLane = distanceToDrive - distanceOnLaneLeft;
            Edge currentEdge = currentLane.getEdge();
            // TODO: decide on which lane to go. atm take any
            Edge nextEdge = currentEdge.getEnd().getEdges().stream().findAny().orElse(null);
            if (nextEdge == null) {
                //throw new RuntimeException("no edge to go to");
                setPosition(0.0);
                return;
            }
            // get first lane
            Lane nextLane = nextEdge.getLanes().stream().findFirst().get();
            if (nextLane == null) {
                throw new RuntimeException("edge has no lanes!");
            }
            setLane(nextLane);
            setPosition(distanceToDriveOnNewLane / nextLane.getLength());
        }
    }
}
