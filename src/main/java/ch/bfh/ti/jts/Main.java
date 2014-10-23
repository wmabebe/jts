package ch.bfh.ti.jts;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import ch.bfh.ti.jts.gui.App;

public class Main {
    
    public static void main(final String[] args) {
        try {
            // set look and feel to native
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            Logger.getLogger(Main.class.getName()).log(Level.WARNING, null, e);
        }
        
        // start app
        App app = new App();
        app.loadNet("map1");
        app.run();
    }
}
