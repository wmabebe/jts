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
     * Factor by which the simulation should take place. 1 means real time
     * speed.
     */
    private final static double  TIME_FACTOR              = 1;
    
    /**
     * The duration of one simulation step in miliseconds. INFO: static here
     * because agent is missing a reference to the simulation object.
     */
    public final static double   SIMULATION_STEP_DURATION = 0.25;
    
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
    
    public Simulation(App app) {
        this(true);
        this.app = app;
    }
    
    public Simulation(boolean doThink) {
        this.doThink = doThink;
    }
    
    public void addCommand(final Command command) {
        commands.add(command);
    }
    
    public Console getConsole() {
        return console;
    }
    
    public void setConsole(final Console console) {
        this.console = console;
    }
    
    /**
     * Do a simulation step
     */
    public void tick(final Net simulateNet) {
        // Time that has passed since the last simulation step [s].
        double timeDelta = simulateNet.tick() * TIME_FACTOR;
        
        // execute all queued commands
        executeCommands(simulateNet);
        
        // simulate
        // delegate simulation to @{link Simulatable}s
        final Layers<Simulatable> simulatables = simulateNet.getSimulatable();
        for (final int layer : simulatables.getLayersIterator()) {
            simulatables.getLayerStream(layer).parallel().forEach(e -> {
                e.simulate(timeDelta);
            });
        }
        
        if (doThink) {
            // think
            simulateNet.getThinkableStream().forEach(e -> {
                assert e != null;
                if (e instanceof Agent) {
                    Agent a = (Agent) e;
                    if (a.isRemoveCandidate()) {
                        // don't call think on this removed agent
                    return;
                }
            }
            e.think();
        }   );
        }
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
    
    public void restart() {
        app.restart();
    }
}
