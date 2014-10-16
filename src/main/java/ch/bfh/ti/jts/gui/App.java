package ch.bfh.ti.jts.gui;

import ch.bfh.ti.jts.ai.agents.FullSpeedAgent;
import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.importer.Importer;
import ch.bfh.ti.jts.simulation.Simulation;

public class App implements Runnable {
    
    public static final boolean DEBUG         = true;
    public static final int     TEST_AGENTS_C = 500;
    private final Importer      importer      = new Importer();
    private final Net           net;
    private final Window        window;
    private final Simulation    simulation;
    
    public App() {
        String mapname = "map1";
        net = importer.importData(String.format("src/main/resources/%s.net.xml", mapname));
        // add some test agents for now
        // TODO: remove when agent spawning is implemented
        for (int i = 0; i < TEST_AGENTS_C; i++) {
            final Agent agent = new FullSpeedAgent();
            // get first lane...
            final Lane lane = (Lane) net.getElementStream().filter(x -> x.getClass() == Lane.class).findAny().get();
            agent.setLane(lane);
            agent.setRelativePosition(Math.random());
            agent.setVelocity(13.8); // 50km/h
            net.addElement(agent);
        }
        window = new Window(net);
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
    
    private void end() {
    }
}
