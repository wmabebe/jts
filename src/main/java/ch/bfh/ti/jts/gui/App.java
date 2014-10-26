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
    private Console            console;
    
    @Override
    public void run() {
        init();
        while (isRunning() && !Thread.interrupted()) {
            simulation.tick();
            window.render();
        }
        end();
    }
    
    public void loadNet(String netName) {
        // import net
        NetImporter netImporter = new NetImporter();
        net = netImporter.importData(String.format("src/main/resources/%s.net.xml", netName));
        
        // import routes data
        RoutesImporter routesImporter = new RoutesImporter();
        routesImporter.setNet(net);
        routes = routesImporter.importData(String.format("src/main/resources/%s.rou.xml", netName));
        net.addRoutes(routes);
    }
    
    private void init() {

        // create simulation
        simulation = new Simulation(net);
        
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
    
    private void end() {
        // free resources or clean up stuff...
    }
}
