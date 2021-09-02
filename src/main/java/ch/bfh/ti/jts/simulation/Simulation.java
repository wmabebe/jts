package ch.bfh.ti.jts.simulation;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.bfh.ti.jts.data.Net;
import ch.bfh.ti.jts.gui.Window;
import ch.bfh.ti.jts.gui.console.Console;
import ch.bfh.ti.jts.gui.console.commands.Command;
import ch.bfh.ti.jts.utils.Config;
import ch.bfh.ti.jts.utils.deepcopy.DeepCopy;
import ch.bfh.ti.jts.utils.layers.Layers;

/**
 * Simulates traffic on a @{link ch.bfh.ti.jts.data.Net}
 *
 * @author Enteee
 * @author winki
 */
public class Simulation {

    /**
     * SaveState holds the simulation data.
     *
     * @author Enteee
     * @author winki
     */
    private class SaveState {

        private final Net saveState;
        private final Net interpolateState;

        public SaveState(final Net state) {
            saveState = DeepCopy.copy(state);
            interpolateState = DeepCopy.copy(state);
        }

        private Net getInterpolateState() {
            return interpolateState;
        }

        private Net getSaveState() {
            return saveState;
        }
    }

    private static final Logger                            log                                 = LogManager.getLogger(Simulation.class);

    /**
     * The 'virtual' duration of one simulation step in seconds. INFO: static
     * here because agent is missing a reference to the simulation object.
     */
    public final static double                             SIMULATION_STEP_DURATION            = Config.getInstance().getDouble("simulation.step.duration", 0.5, 0.0, 3600.0);
    /**
     * A factor which accelerates wallclock time. For faster rendering progress.
     * 1 := WallclockTime = PhysicalTime
     */
    private static final double                            WALL_CLOCK_ACCELERATION_FACTOR      = Config.getInstance().getDouble("simulation.wallclock.factor", 1.0, 0.0000000001, 10000000000.0);
    /**
     * Minimum gap between wall clock time and simulation time before stopping
     * [s].
     */
    private static final double                            MIN_SIMULATION_WALL_CLOCK_GAP       = Config.getInstance().getDouble("simulation.wallclock.gap", 20.0, 0.0, 3600.0);
    /**
     * Keep the last {@link Net} for this amount of time in [s];
     */
    private static final double                            SIMULATION_HISTORY_KEEP_WINDOW      = Config.getInstance().getDouble("simulation.history.time", 10.0, 0.0, 3600.0);
    /**
     * Size of the floating average for tick duration.
     */
    private static final int                               FLOAT_AVERAGE_TICK_DURATION_SIZE    = Config.getInstance().getInt("simulation.tick.averagetime", 20, 0, 1000);
    /**
     * Start wallclock time of the simulation [s].
     */
    private final double                                   startWallClockTime                  = System.nanoTime() * 1E-9;
    /**
     * Queue used for floating average calcuateion of tick duration [s].
     */
    private final Queue<Double>                            floatAverageTickDurationQueue       = new CircularFifoQueue<>(FLOAT_AVERAGE_TICK_DURATION_SIZE);
    /**
     * Simulation states. Whereas Key = absolute simulation time
     */
    private final ConcurrentSkipListMap<Double, SaveState> simulationStates                    = new ConcurrentSkipListMap<>();
    /**
     * Interpolate the wall clock state.
     */
    private final AtomicBoolean                            interpolateWallClockSimulationState = new AtomicBoolean(Config.getInstance().getValue("simulation.wallclock.interpolate", true));
    /**
     * Allow collisions.
     */
    private boolean                                        allowCollisions                     = Config.getInstance().getValue("simulation.collisions", true);
    /**
     * The @{link Net} to simulate.
     */
    final Net                                              simulateNet;

    public Simulation(final Net simulateNet) {
        this.simulateNet = simulateNet;
    }
    
    public Net getSimNet() {
        return this.simulateNet;
    }

    private void addSimulationState(final Net net) {
        final Net netCopy = DeepCopy.copy(net);
        // remove old states from history
        final double simulationStatesWindowMin = getWallClockTime() - SIMULATION_HISTORY_KEEP_WINDOW;
        simulationStates.headMap(simulationStatesWindowMin).forEach((key, value) -> {
            simulationStates.remove(key, value);
        });

        simulationStates.put(netCopy.getSimulationTime(), new SaveState(net));
        log.debug("simulationStates.size:" + simulationStates.size());
    }

    /**
     * command on simulation element
     *
     * @param command
     */
    public void executeCommand(final Command command) {
        final Class<?> targetType = command.getTargetType();
        final Console console = Window.getInstance().getConsole();
        if (targetType == Simulation.class) {
            // command on simulation itself
            command.execute(this).ifPresent(message -> console.write(message));
        } else {
            simulateNet.getElementStream(command.getTargetType()).forEach(element -> {
                command.execute(element).ifPresent(message -> console.write(message));
            });
        }
    }

    public NavigableMap<Double, Net> getSavedStates() {
        // TODO: do with streams
        final NavigableMap<Double, Net> returnSavedStates = new TreeMap<>();
        for (final Entry<Double, SaveState> entry : simulationStates.entrySet()) {
            final Net savedNet = entry.getValue().getSaveState();
            returnSavedStates.put(savedNet.getSimulationTime(), savedNet);
        }
        return returnSavedStates;
    }

    /**
     * blocks if there is no simulation state or wall clock is ahead of
     * simulation
     *
     * @return the simulation state closest to the wall clock time.
     */
    public Net getWallCLockSimulationState() {
        Net wallClockSimulationState = null;
        do {
            final Entry<Double, SaveState> entry = simulationStates.floorEntry(getWallClockTime());
            if (entry != null) {
                wallClockSimulationState = entry.getValue().getInterpolateState();
            }
        } while (wallClockSimulationState == null);
        if (interpolateWallClockSimulationState.get()) {
            simulate(wallClockSimulationState, getWallClockTime() - wallClockSimulationState.getSimulationTime());
        }
        return wallClockSimulationState;
    }

    /**
     * @return wall clock time spent in [s].
     */
    public double getWallClockTime() {
        return (System.nanoTime() * 1E-9 - startWallClockTime) * WALL_CLOCK_ACCELERATION_FACTOR;
    }

    public boolean isAllowCollisions() {
        return allowCollisions;
    }

    /**
     * Flushes all the buffered simulation states.
     */
    public void resetSimulation() {
        simulationStates.clear();
    }

    public void setAllowCollisions(final boolean allowCollisions) {
        this.allowCollisions = allowCollisions;
    }

    public void setInterpolateWallClockSimulationState(final boolean value) {
        interpolateWallClockSimulationState.set(value);
    }

    /**
     * Simulate the given net.
     *
     * @param simulateNet
     * @param duration
     */
    private void simulate(final Net simulateNet, final double duration) {
        // delegate simulation to @{link Simulatable}s
        final Layers<Simulatable> simulatables = simulateNet.getSimulatable();
        for (final int layer : simulatables.getLayersIterator()) {
            simulatables.getLayerStream(layer).parallel().forEach(e -> {
                e.simulate(duration);
            });
        }
    }

    /**
     * Think on the given net
     *
     * @param simulateNet
     * @param duration
     */
    private void think(final Net simulateNet, final double duration) {
        simulateNet.getThinkableStream().forEach(element -> {
            // think
            try {
                element.think();
            } catch (final Exception e) {
                log.error("Think failed for element: " + element, e);
            }
        });
    }

    /**
     * Do a simulation step. Blocks if simulation is too far away from wall
     * clock.
     */
    public void tick() {
        
        final double tickTimeStart = getWallClockTime();
        simulate(simulateNet, SIMULATION_STEP_DURATION);
        think(simulateNet, SIMULATION_STEP_DURATION);
        addSimulationState(simulateNet);
        floatAverageTickDurationQueue.add(getWallClockTime() - tickTimeStart);
        // we have enough for floating average
        if (floatAverageTickDurationQueue.size() >= FLOAT_AVERAGE_TICK_DURATION_SIZE) {
            // get average wall tlock time of one loop
            final double floatAverageLoopDuration = floatAverageTickDurationQueue.stream().mapToDouble(x -> {
                return x;
            }).average().orElse(0);
            final double simulationMinAdvance = Math.max(MIN_SIMULATION_WALL_CLOCK_GAP, floatAverageLoopDuration);
            final double simulationWallClockDiff = simulateNet.getSimulationTime() - getWallClockTime();
            final double simulationAdvancedTooMuch = simulationWallClockDiff - simulationMinAdvance;
            // simulation is in advance too much
            if (simulationAdvancedTooMuch > 0) {
                try {
                    log.debug("Tick sleep for " + simulationAdvancedTooMuch + " s simulationWallClockDiff:" + simulationWallClockDiff + " s floatAverageWallClockLoopDuration:"
                            + floatAverageLoopDuration + " s");
                    Thread.sleep((long) (simulationAdvancedTooMuch * 1E3));
                } catch (final InterruptedException e) {
                    log.warn("Tick sleep interrupted");
                }
            }
        }
    }
}
