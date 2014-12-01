package ch.bfh.ti.jts;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.bfh.ti.jts.console.Console;
import ch.bfh.ti.jts.console.JtsConsole;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.data.SpawnInfo;
import ch.bfh.ti.jts.gui.Window;
import ch.bfh.ti.jts.importer.NetImporter;
import ch.bfh.ti.jts.importer.RoutesImporter;
import ch.bfh.ti.jts.simulation.Simulation;

public class App implements Runnable {
    
    public static final boolean   DEBUG     = true;
    
    public boolean                isRunning = false;
    private Net                   net;
    private Collection<SpawnInfo> routes;
    private Window                window;
    private Simulation            simulation;
    private Console               console;
    
    private String                netName;
    
    private void end() {
        // free resources or clean up stuff...
    }
    
    private void init() {
        
        // create simulation
        simulation = new Simulation(this);
        
        // create console
        console = new JtsConsole();
        console.setSimulation(simulation);
        
        // create window
        window = new Window(net, console);
        
        isRunning = true;
        window.setVisible(true);
    }
    
    public void restart() {
        
        reloadNet();
        
        // create simulation
        simulation = new Simulation(this);
        console.setSimulation(simulation);
    }
    
    private boolean isRunning() {
        return isRunning;
    }
    
    public void reloadNet() {
        loadNet(null);
    }
    
    public void loadNet(final String netName) {
        if (netName != null) {
            this.netName = netName;
        }
        
        // import net
        final NetImporter netImporter = new NetImporter();
        net = netImporter.importData(String.format("src/main/resources/%s.net.xml", this.netName));
        
        // import routes data
        final RoutesImporter routesImporter = new RoutesImporter();
        routesImporter.setNet(net);
        routes = routesImporter.importData(String.format("src/main/resources/%s.rou.xml", this.netName));
        net.addRoutes(routes);
    }
    
    @Override
    public void run() {
        init();
        while (isRunning() && !Thread.interrupted()) {
            simulation.tick(net);
            window.setNet(net);
            // Sleep some time
            try {
                Thread.sleep((int) (Simulation.SIMULATION_STEP_DURATION * 1000));
            } catch (InterruptedException e) {
                Logger.getLogger(App.class.getName()).log(Level.WARNING, "Thread interrupted.", e);
            }
        }
        end();
    }
}
