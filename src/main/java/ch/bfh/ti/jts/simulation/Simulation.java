package ch.bfh.ti.jts.simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    private final static double          TIME_FACTOR = 1;
    /**
     * Net for which to simulate traffic.
     */
    private final Net                    simulateNet;
    /**
     * Absolute time at which the simulation started (nanoseconds).
     */
    private long                         startTime;
    /**
     * Absolute time at which the the lastest simulation step took place
     * (nanoseconds).
     */
    private long                         lastTick;
    /**
     * Time that has passed since the last simulation step [s].
     */
    private double                       timeDelta;
    /**
     * Commands the simulation should execute.
     */
    private final BlockingQueue<Command> commands    = new LinkedBlockingQueue<>();
    
    private Console                      console;
    
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
        // poll all commands
        final Map<Class<?>, List<Command>> broadcastCommand = new HashMap<>();
        final Map<Integer, List<Command>> dedicatedCommands = new HashMap<>();
        while (!commands.isEmpty()) {
            Command command = commands.poll();
            List<Command> commandList;
            if (command.isBroadcastCommand()) {
                commandList = broadcastCommand.get(command.getTargetType());
                if (commandList == null) {
                    commandList = new LinkedList<>();
                    broadcastCommand.put(command.getTargetType(), commandList);
                }
            } else {
                commandList = dedicatedCommands.get(command.getTargetElement());
                if (commandList == null) {
                    commandList = new LinkedList<>();
                    dedicatedCommands.put(command.getTargetElement(), commandList);
                }
            }
            commandList.add(command);
        }
        // run broadcast commands
        broadcastCommand.entrySet().forEach(entry -> {
            final Class<?> clazz = entry.getKey();
            final List<Command> commands = entry.getValue();
            simulateNet.getElementStream(clazz).forEach(element -> {
                commands.forEach(command -> {
                    command.execute(element);
                });
            });
            
        });
        // TODO: run dedicated commands
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
        
        // set lastTick for time diff
        lastTick = now;
    }
    
    public BlockingQueue<Command> getCommands() {
        return commands;
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
