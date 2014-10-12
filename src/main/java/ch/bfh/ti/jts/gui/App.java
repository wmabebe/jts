package ch.bfh.ti.jts.gui;

import java.awt.Graphics2D;

import ch.bfh.ti.jts.ai.agents.RandomAgent;
import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.importer.Importer;
import ch.bfh.ti.jts.simulation.Simulation;

public class App implements Runnable {
    
    public static final boolean DEBUG         = true;
    public static final int     TEST_AGENTS_C = 5;
    private final Importer      importer      = new Importer();
    private final Net           net;
    private final Window        window;
    private final Simulation    simulation;
    
    public App() {
        String mapname = "net";
        net = importer.importData(String.format("src/main/resources/%s.net.xml", mapname));
        // add some test agents for now
        // TODO: remove when agent spawning is implemented
        for (int i = 0; i < TEST_AGENTS_C; i++) {
            final Agent agent = new RandomAgent();
            // get first lane...
            final Lane lane = (Lane) net.getElementStream().filter(x -> x.getClass() == Lane.class).findAny().get();
            agent.setLane(lane);
            agent.setPosition(Math.random());
            agent.setVelocity(10);
            net.addElement(agent);
        }
        window = new Window(g -> {
            render(g);
        });
        simulation = new Simulation(net);
    }
    
    @Override
    public void run() {
        init();
        while (isRunning() && !Thread.interrupted()) {
            simulation.tick();
            window.render();
        }
        end();
    }
    
    private void init() {
        window.setVisible(true);
    }
    
    private boolean isRunning() {
        return true;
    }
    
    private void render(final Graphics2D g) {
        /*
         * Hotfix: affine transformation y = -y. We've to do this because the
         * coordinates imported expect a origin in the left bottom corner. But
         * java does stuff different. Therefore the origin is in the left upper
         * corner. As a result all the agents are driving on the wrong side.
         * TODO: Change importer so that the y coordinates get transformed.
         */
        // g.transform(new AffineTransform().getScaleInstance(1, -1));
        net.render(g);
    }
    
    private void end() {
    }
}
