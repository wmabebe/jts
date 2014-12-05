package ch.bfh.ti.jts;

import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    
    public final static Logger LOG = LogManager.getLogger(Main.class);
    
    public static void main(final String[] args) {
        
        try {
            // set look and feel to native
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            LOG.warn("failed to set look and feel", e);
        }
        
        // start app
        final App app = new App();
        app.loadNet("mini_round");
        app.run();
    }
}
