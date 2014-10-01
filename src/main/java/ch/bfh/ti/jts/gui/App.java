package ch.bfh.ti.jts.gui;

import java.awt.Graphics2D;

import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.importer.Importer;

public class App {
    
    private final Window window;
    private final Net    net;
    
    public App() {
        net = new Importer().importData("src\\main\\resources\\net.net.xml");
        window = new Window(g -> {
            render(g);
        });
    }
    
    public void run() {
        init();
        while (isRunning() && !Thread.interrupted()) {
            update();
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
    
    private void update() {
    }
    
    private void render(final Graphics2D g) {
        net.render(g);
    }
    
    private void end() {
    }
}
