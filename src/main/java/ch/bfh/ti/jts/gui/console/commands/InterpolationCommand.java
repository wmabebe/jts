package ch.bfh.ti.jts.gui.console.commands;

import java.util.Optional;

import ch.bfh.ti.jts.simulation.Simulation;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Set animation interpolation setting")
public class InterpolationCommand extends Command {

    @Parameter(names = { "-off", "-value" }, description = "Disable interpolation")
    private boolean off = false;

    @Override
    public Optional<String> execute(final Object executor) {
        final Simulation simulation = (Simulation) executor;
        Optional<String> message = Optional.empty();
        if (!off) {
            simulation.setInterpolateWallClockSimulationState(true);
            message = Optional.of("Interpolation on");
        } else {
            simulation.setInterpolateWallClockSimulationState(false);
            off = false; // reset value
            message = Optional.of("Interpolation off");
        }
        return message;
    }

    @Override
    public String getName() {
        return "interpolation";
    }

    @Override
    public Class<?> getTargetType() {
        return Simulation.class;
    }
}
