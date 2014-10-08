package ch.bfh.ti.jts.gui;

import java.awt.Graphics2D;

import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.importer.Importer;
import ch.bfh.ti.jts.simulation.Simulation;

public class App implements Runnable {
    
    public static final boolean DEBUG = true;
    
    private final Importer importer = new Importer();
    private final Net           net;
    private final Window        window;
    private final Simulation    simulation;
    
    public App() {
        net = importer.importData("src/main/resources/net.net.xml");
        importer.addTestAgents(net);
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
