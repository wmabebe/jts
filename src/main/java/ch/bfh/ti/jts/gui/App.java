package ch.bfh.ti.jts.gui;

import java.awt.Graphics2D;

import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.importer.Importer;
import ch.bfh.ti.jts.simulation.Simulation;

public class App implements Runnable {
    
    public static final boolean DEBUG    = true;
    private final Importer      importer = new Importer();
    private final Net           net;
    private final Window        window;
    private final Simulation    simulation;
    
    public App() {
        net = importer.importData("src/main/resources/net.net.xml");
        // add some test agents for now
        // TODO: remove when agent spawning is implemented
        final int numAgents = 50;
        for (int i = 0; i < numAgents; i++) {
            final Agent agent = new Agent();
            // get first lane...
            final Lane lane = (Lane) net.getElementStream().filter(x -> x.getClass() == Lane.class).findAny().get();
            agent.setLane(lane);
            agent.setPosition(Math.random());
            agent.setVelocity(0.1);
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
        net.render(g);
    }
    
    private void end() {
    }
}
