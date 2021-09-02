package ch.bfh.ti.jts;

import java.awt.Font;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.data.SpawnInfo;
import ch.bfh.ti.jts.gui.Window;
import ch.bfh.ti.jts.gui.console.Console;
import ch.bfh.ti.jts.gui.console.commands.Command;
import ch.bfh.ti.jts.importer.NetImporter;
import ch.bfh.ti.jts.importer.RoutesImporter;
import ch.bfh.ti.jts.simulation.Simulation;
import ch.bfh.ti.jts.utils.Config;

/**
 * Runnable application class.
 *
 * @author Enteee
 * @author winki
 */
public class App implements Runnable {

    public static App getInstance() {
        return instance;
    }

    public static final Font     FONT               = new Font(Config.getInstance().getValue("app.font.familiy", "sans-serif"), Font.PLAIN, Config.getInstance().getInt("app.font.size", 4, 1, 100));
    /**
     * Format string used for net loading.
     */
    private static String        NET_LOAD_FORMAT    = Config.getInstance().getValue("path.net", "src/main/resources/%s.net.xml");
    /**
     * Format string used for routes loading.
     */
    private static final String  ROUTES_LOAD_FORMAT = Config.getInstance().getValue("path.routes", "src/main/resources/%s.rou.xml");
    /**
     * Commands the simulation should execute.
     */
    private final Queue<Command> commands           = new ConcurrentLinkedQueue<>();

    /**
     * Singleton
     */
    private static App           instance           = new App();
    public boolean               isRunning          = false;
    private String               netName;
    private Simulation           simulation;
    
    private int TIME_LIMIT = 1000;

    public void addCommand(final Command command) {
        commands.add(command);
    }

    private void end() {
        // free resources or clean up stuff...
    }

    private void executeCommands() {
        while (commands.size() > 0) {
            final Command command = commands.poll();
            command.getTargetType();
            final Console console = Window.getInstance().getConsole();
            if (command.getTargetType() == App.class) {
                // command for app
                command.execute(this).ifPresent(message -> console.write(message));
            } else {
                // delegate to simulation
                simulation.executeCommand(command);
            }
        }
    }

    public Simulation getSimulation() {
        return simulation;
    }

    private void init() {
        if (simulation == null) {
            throw new RuntimeException("simulation not loaded");
        }
        Window.getInstance().setVisible(true);
    }

    private boolean isRunning() {
        return isRunning;
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

    public void restart() {
        loadSimulation(netName); // load same net again
    }

    @Override
    public void run() {
        isRunning = true;
        init();
        int i = 0;
        final long startTime = System.currentTimeMillis();
        while (isRunning() && !Thread.interrupted() && i++ <= TIME_LIMIT) {
            executeCommands();
            simulation.tick();
            simulation.getSimNet().addToAgentQueues();
        }
        System.out.println("Logging...");
        Handshake.logHandshakes(this.simulation,startTime,TIME_LIMIT);
        System.out.println("Logging complete.");
        end();
    }
}
