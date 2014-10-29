package ch.bfh.ti.jts.simulation;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ch.bfh.ti.jts.console.Console;
import ch.bfh.ti.jts.console.commands.Command;
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
    private final static double  TIME_FACTOR = 1;
    /**
     * Net for which to simulate traffic.
     */
    private final Net            simulateNet;
    /**
     * Absolute time at which the simulation started (nanoseconds).
     */
    private long                 startTime;
    /**
     * Absolute time at which the the lastest simulation step took place
     * (nanoseconds).
     */
    private long                 lastTick;
    /**
     * Time that has passed since the last simulation step [s].
     */
    private double               timeDelta;
    /**
     * Commands the simulation should execute.
     */
    private final Queue<Command> commands    = new ConcurrentLinkedQueue<>();
    
    private Console              console;
    
    public Simulation(final Net simulateNet) {
        this.simulateNet = simulateNet;
        start();
    }
    
    public void start() {
        startTime = System.nanoTime();
        lastTick = startTime;
    }
    
    /**
     * Do a simulation step
     */
    public void tick() {
        // do time calculations
        final long now = System.nanoTime();
        timeDelta = (now - lastTick) * 1E-9 * TIME_FACTOR;
        // execute all commands
        while (commands.size() > 0) {
            Command command = commands.poll();
            Class<?> targetType = command.getTargetType();
            simulateNet.getElementStream(targetType).forEach(element -> {
                getConsole().write(command.execute(element));
            });
        }
        // think
        simulateNet.getThinkableStream().forEach(e -> {
            e.think();
        });
        
        // simulate
        // delegate simulation for all simulatables
        final Layers<Simulatable> simulatables = simulateNet.getSimulatable();
        for (int layer : simulatables.getLayersIterator()) {
            simulatables.getLayerStream(layer).sequential().forEach(e -> {
                e.simulate(timeDelta);
            });
        }
        
        // set lastTick for time difference
        lastTick = now;
    }
    
    public void addCommand(final Command command) {
        commands.add(command);
    }
    
    public Console getConsole() {
        return console;
    }
    
    public void setConsole(Console console) {
        this.console = console;
    }
    
    public Net getNet() {
        return simulateNet;
    }
}
