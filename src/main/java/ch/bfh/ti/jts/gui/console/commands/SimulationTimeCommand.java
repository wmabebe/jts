package ch.bfh.ti.jts.gui.console.commands;

import java.util.Optional;

import ch.bfh.ti.jts.data.Net;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Show simulation time")
public class SimulationTimeCommand extends Command {

    @Override
    public Optional<String> execute(final Object executor) {
        final Net net = (Net) executor;
        return Optional.of(String.format("Simulation time: %.5f seconds", net.getSimulationTime()));
    }

    @Override
    public String getName() {
        return "stime";
    }

    @Override
    public Class<?> getTargetType() {
        return Net.class;
    }
}
