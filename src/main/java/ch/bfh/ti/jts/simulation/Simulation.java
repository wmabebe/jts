package ch.bfh.ti.jts.simulation;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ch.bfh.ti.jts.App;
import ch.bfh.ti.jts.console.Console;
import ch.bfh.ti.jts.console.commands.Command;
import ch.bfh.ti.jts.data.Agent;
import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.utils.layers.Layers;

/**
 * Simulates traffic on a @{link ch.bfh.ti.jts.data.Net}
 *
 * @author ente
 */
public class Simulation {
    
    /**
     * The 'virtual' duration of one simulation step in seconds. INFO: static
     * here because agent is missing a reference to the simulation object.
     */
    public final static double   SIMULATION_STEP_DURATION = 1;
    
    /**
     * Commands the simulation should execute.
     */
    private final Queue<Command> commands                 = new ConcurrentLinkedQueue<>();
    
    private Console              console;
    
    /**
     * If the simulation should call the think method of each agent in every
     * step. If false, the simulation is "dumb" and does only the basic physics
     * (for example the gui thread).
     */
    private final boolean        doThink;
    
    private App                  app;
    
    public Simulation(final App app) {
        this(true);
        this.app = app;
    }
    
    public Simulation(final boolean doThink) {
        this.doThink = doThink;
    }
    
    public void addCommand(final Command command) {
        commands.add(command);
    }
    
    private void executeCommands(final Net simulateNet) {
        while (commands.size() > 0) {
            final Command command = commands.poll();
            final Class<?> targetType = command.getTargetType();
            if (targetType == Simulation.class) {
                // command on simulation itself
                getConsole().write(command.execute(this));
            } else {
                // command on simulation element
                simulateNet.getElementStream(targetType).forEach(element -> {
                    getConsole().write(command.execute(element));
                });
            }
        }
    }
    
    public Console getConsole() {
        return console;
    }
    
    public void restart() {
        app.restart();
    }
    
    public void setConsole(final Console console) {
        this.console = console;
    }
    
    /**
     * Do a simulation step
     * 
     * @param simulateNet
     *            for this net
     * @param duration
     *            for this duration [s]
     */
    public void tick(final Net simulateNet, final double duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("duration");
        }
        if (simulateNet == null) {
            throw new IllegalArgumentException("simulateNet");
        }
        
        // execute all queued commands
        executeCommands(simulateNet);
        
        // simulate
        // delegate simulation to @{link Simulatable}s
        final Layers<Simulatable> simulatables = simulateNet.getSimulatable();
        for (final int layer : simulatables.getLayersIterator()) {
            simulatables.getLayerStream(layer).parallel().forEach(e -> {
                e.simulate(duration);
            });
        }
        
        if (doThink) {
            // think
            simulateNet.getThinkableStream().forEach(e -> {
                assert e != null;
                if (e instanceof Agent) {
                    final Agent a = (Agent) e;
                    // don't call think on this removed agent
                    if (a.isRemoveCandidate()) {
                        return;
                    }
                }
                e.think();
            });
        }
    }
}
