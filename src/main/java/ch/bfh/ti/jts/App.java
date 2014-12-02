package ch.bfh.ti.jts;

import java.util.Collection;
import java.util.Queue;
import java.util.logging.Logger;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import ch.bfh.ti.jts.console.Console;
import ch.bfh.ti.jts.console.JtsConsole;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.data.SpawnInfo;
import ch.bfh.ti.jts.gui.Window;
import ch.bfh.ti.jts.importer.NetImporter;
import ch.bfh.ti.jts.importer.RoutesImporter;
import ch.bfh.ti.jts.simulation.Simulation;

public class App implements Runnable {
    
    public static final boolean   DEBUG                                       = false;
    
    /**
     * Size of the floating average for wall clock loop duration.
     */
    private static final int      FLOAT_AVERAGE_WALL_CLOCK_LOOP_DURATION_SIZE = 20;
    /**
     * Wall clock time the simulation is in advance before stopping [s].
     */
    private static final double   SIMULATION_ADVANCED                         = 20;
    
    public boolean                isRunning                                   = false;
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
        window = new Window(console);
        
        isRunning = true;
        window.setVisible(true);
    }
    
    private boolean isRunning() {
        return isRunning;
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
    
    public void reloadNet() {
        loadNet(null);
    }
    
    public void restart() {
        reloadNet();
        // create simulation
        simulation = new Simulation(this);
        console.setSimulation(simulation);
    }
    
    @Override
    public void run() {
        init();
        // floating average wall clock time of one loop [s]
        Queue<Double> floatAverageWallClockLoopDurationQueue = new CircularFifoQueue<>(FLOAT_AVERAGE_WALL_CLOCK_LOOP_DURATION_SIZE);
        while (isRunning() && !Thread.interrupted()) {
            final double wallClockStart = window.getWallClockTime();
            simulation.tick(net, Simulation.SIMULATION_STEP_DURATION);
            window.addNet(net);
            floatAverageWallClockLoopDurationQueue.add(window.getWallClockTime() - wallClockStart);
            // we have enough for floating average
            if (floatAverageWallClockLoopDurationQueue.size() == FLOAT_AVERAGE_WALL_CLOCK_LOOP_DURATION_SIZE) {
                // get average wall tlock time of one loop
                final double floatAverageWallClockLoopDuration = floatAverageWallClockLoopDurationQueue.stream().mapToDouble(x -> {
                    return x;
                }).average().orElse(0);
                final double simulationMinAdvance = Math.max(SIMULATION_ADVANCED, floatAverageWallClockLoopDuration);
                final double simulationWallClockDiff = net.getSimulationTime() - window.getWallClockTime();
                final double simulationAdvancedTooMuch = simulationWallClockDiff - simulationMinAdvance;
                // simulation is in advance too much
                if (simulationAdvancedTooMuch > 0) {
                    try {
                        if (App.DEBUG) {
                            Logger.getLogger(App.class.getName()).info(
                                    "App sleep:" + simulationAdvancedTooMuch + " s simulationWallClockDiff:" + simulationWallClockDiff + " s floatAverageWallClockLoopDuration:"
                                            + floatAverageWallClockLoopDuration + " s");
                        }
                        Thread.sleep((long) (simulationAdvancedTooMuch * 1E3));
                    } catch (InterruptedException e) {
                        Logger.getLogger(App.class.getName()).warning("Thread sleep interrupted");
                    }
                }
            }
        }
        end();
    }
}
