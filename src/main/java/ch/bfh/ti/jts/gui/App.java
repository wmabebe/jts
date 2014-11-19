package ch.bfh.ti.jts.gui;

import java.util.Collection;

import ch.bfh.ti.jts.console.Console;
import ch.bfh.ti.jts.console.JtsConsole;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.data.Route;
import ch.bfh.ti.jts.importer.NetImporter;
import ch.bfh.ti.jts.importer.RoutesImporter;
import ch.bfh.ti.jts.simulation.Simulation;

public class App implements Runnable {
    
    public static final boolean DEBUG     = true;
    
    public boolean              isRunning = false;
    private Net                 net;
    private Collection<Route>   routes;
    private Window              window;
    private Simulation          simulation;
    private Console             console;
    private int                 simulationStepDuration;
    
    public App() {
        this.simulationStepDuration = 1000; // one second is default
    }
    
    private void end() {
        // free resources or clean up stuff...
    }
    
    private void init() {
        
        // create simulation
        simulation = new Simulation();
        
        // create console
        console = new JtsConsole();
        console.setSimulation(simulation);
        
        // create window
        window = new Window(net, console);
        
        isRunning = true;
        window.setVisible(true);
    }
    
    private boolean isRunning() {
        return isRunning;
    }
    
    public void loadNet(final String netName) {
        // import net
        final NetImporter netImporter = new NetImporter();
        net = netImporter.importData(String.format("src/main/resources/%s.net.xml", netName));
        
        // import routes data
        final RoutesImporter routesImporter = new RoutesImporter();
        routesImporter.setNet(net);
        routes = routesImporter.importData(String.format("src/main/resources/%s.rou.xml", netName));
        net.addRoutes(routes);
    }
    
    /**
     * Set the duration of one simulation step in miliseconds.
     * 
     * @param simulationStepDuration
     *            duration in miliseconds
     */
    public void setSimulationStepDuration(int simulationStepDuration) {
        this.simulationStepDuration = simulationStepDuration;
    }
    
    @Override
    public void run() {
        init();
        while (isRunning() && !Thread.interrupted()) {
            simulation.tick(net);
            window.setNet(net);
            // Sleep some time
            try {
                Thread.sleep(simulationStepDuration);
            } catch (InterruptedException e) {
            }
        }
        end();
    }
}
