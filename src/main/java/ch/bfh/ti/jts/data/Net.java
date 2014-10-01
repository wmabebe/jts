package ch.bfh.ti.jts.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;

import ch.bfh.ti.jts.gui.Renderable;

public class Net extends Element implements Renderable {
    
    private final Collection<Agent>    agents;
    private final Collection<Edge>     edges;
    private final Collection<Junction> junctions;
    private final Collection<Lane>     lanes;
    
    public Net() {
        agents = new LinkedList<Agent>();
        edges = new LinkedList<Edge>();
        junctions = new LinkedList<Junction>();
        lanes = new LinkedList<Lane>();
    }
    
    public Collection<Agent> getAgents() {
        return agents;
    }
    
    public Collection<Edge> getEdges() {
        return edges;
    }
    
    public Collection<Junction> getJunctions() {
        return junctions;
    }
    
    public Collection<Lane> getLanes() {
        return lanes;
    }
    
    @Override
    public void render(final Graphics2D g) {
        g.setStroke(new BasicStroke(6));
        g.setColor(Color.BLACK);
        for (final Edge edge : edges) {
            edge.render(g);
        }
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.BLACK);
        for (final Junction junction : junctions) {
            junction.render(g);
        }
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.LIGHT_GRAY);
        for (final Lane lane : lanes) {
            lane.render(g);
        }
    }
}
