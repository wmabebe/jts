package ch.bfh.ti.jts;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import ch.bfh.ti.jts.ai.agents.RealisticAgent;
import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.gui.Window;
import ch.bfh.ti.jts.importer.NetImporter;

public class Main {
    
    public static void main(final String[] args) {
        // configure loggers
        Logger.getLogger(Main.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(App.class.getName()).setLevel(Level.ALL);
        Logger.getLogger(Window.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(Agent.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(Lane.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(Net.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(NetImporter.class.getName()).setLevel(Level.OFF);
        Logger.getLogger(RealisticAgent.class.getName()).setLevel(Level.OFF);
        try {
            // set look and feel to native
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.WARNING, "failed to set look and feel", e);
        }
        
        // start app
        final App app = new App();
        app.loadNet("round");
        app.run();
    }
}
