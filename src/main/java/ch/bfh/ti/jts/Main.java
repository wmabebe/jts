package ch.bfh.ti.jts;

import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bfh.ti.jts.utils.Config;

/**
 * Entry point class with the main method.
 * 
 * @author Enteee
 * @author winki
 */
public class Main {
    
    private static final Logger log   = LogManager.getLogger(Main.class);
    
    /**
     * Debug mode
     */
    public static final boolean DEBUG = Config.getInstance().getValue("app.debug", false);
    
    public static void main(final String[] args) {
        
        try {
            // set look and feel to native
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            log.warn("Failed to set look and feel", e);
        }
        
        // load configuration
        String net = Config.getInstance().getValue("net.name.default", "default");
        
        // start app
        final App app = App.getInstance();
        app.loadSimulation(net);
        app.run();
    }
}
