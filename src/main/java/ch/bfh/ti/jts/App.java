package ch.bfh.ti.jts;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.data.SpawnInfo;
import ch.bfh.ti.jts.gui.Window;
import ch.bfh.ti.jts.gui.console.Console;
import ch.bfh.ti.jts.gui.console.commands.Command;
import ch.bfh.ti.jts.importer.NetImporter;
import ch.bfh.ti.jts.importer.RoutesImporter;
import ch.bfh.ti.jts.simulation.Simulation;

public class App implements Runnable {
    
    public final static Logger   LOG                = LogManager.getLogger(App.class);
    /**
     * Format string used for net loading.
     */
    private static final String  NET_LOAD_FORMAT    = "src/main/resources/%s.net.xml";
    /**
     * Format string used for routes loading.
     */
    private static final String  ROUTES_LOAD_FORMAT = "src/main/resources/%s.rou.xml";
    /**
     * Commands the simulation should execute.
     */
    private final Queue<Command> commands           = new ConcurrentLinkedQueue<>();
    /**
     * Singleton
     */
    private static App           instance           = new App();
    
    public static App getInstance() {
        return instance;
    }
    public boolean     isRunning = false;
    private String     netName;
    private Simulation simulation;
    
    private App() {
        // singleton
    }
    
    public Simulation getSimulation() {
        return simulation;
    }
    
    public void loadSimulation(final String netName) {
        if (netName == null) {
            throw new IllegalArgumentException("netName");
        }
        this.netName = netName;
        
        // import net
        final NetImporter netImporter = new NetImporter();
        final Net net = netImporter.importData(String.format(NET_LOAD_FORMAT, this.netName));
        
        // import routes data
        final RoutesImporter routesImporter = new RoutesImporter();
        routesImporter.setNet(net);
        final Collection<SpawnInfo> routes = routesImporter.importData(String.format(ROUTES_LOAD_FORMAT, this.netName));
        net.addRoutes(routes);
        simulation = new Simulation(net);
    }
    
    private void end() {
        // free resources or clean up stuff...
    }
    
    private void init() {
        if (simulation == null) {
            throw new RuntimeException("Simulation not loaded");
        }
        Window.getInstance().setVisible(true);
    }
    
    private boolean isRunning() {
        return isRunning;
    }
    
    public void restart() {
        loadSimulation(netName); // load same net again
    }
    
    @Override
    public void run() {
        isRunning = true;
        init();
        while (isRunning() && !Thread.interrupted()) {
            executeCommands();
            simulation.tick();
        }
        end();
    }
    
    private void executeCommands() {
        while (commands.size() > 0) {
            final Command command = commands.poll();
            final Class<?> targetType = command.getTargetType();
            final Console console = Window.getInstance().getConsole();
            if (command.getTargetType() == App.class) {
                // command for app
                console.write(command.execute(this));
            } else {
                // delegate to simulation
                simulation.executeCommand(command);
            }
        }
    }
    
    public void addCommand(final Command command) {
        commands.add(command);
    }
}
