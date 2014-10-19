package ch.bfh.ti.jts.gui;

import java.util.Collection;

import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.data.Route;
import ch.bfh.ti.jts.importer.NetImporter;
import ch.bfh.ti.jts.importer.RoutesImporter;
import ch.bfh.ti.jts.simulation.Simulation;

public class App implements Runnable {
    
    public static final boolean     DEBUG         = true;
    public static final int         TEST_AGENTS_C = 200;
    private final Net               net;
    private final Collection<Route> routes;
    private final Window            window;
    private final Simulation        simulation;
    
    public App() {
        // import net and routes data...
        String mapname = "map1";
        NetImporter netImporter = new NetImporter();
        net = netImporter.importData(String.format("src/main/resources/%s.net.xml", mapname));
        RoutesImporter routesImporter = new RoutesImporter();
        routesImporter.setNet(net);
        routes = routesImporter.importData(String.format("src/main/resources/%s.rou.xml", mapname));
        net.addRoutes(routes);
        // open window...
        window = new Window(net);
        // start simulation...
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
