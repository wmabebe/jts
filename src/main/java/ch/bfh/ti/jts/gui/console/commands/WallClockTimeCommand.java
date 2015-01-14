package ch.bfh.ti.jts.gui.console.commands;

import java.util.Optional;

import ch.bfh.ti.jts.simulation.Simulation;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Show the wall clock time")
public class WallClockTimeCommand extends Command {
    
    @Override
    public Optional<String> execute(final Object executor) {
        final Simulation simulation = (Simulation) executor;
        return Optional.of(String.format("Wall clock time: %.5f seconds", simulation.getWallClockTime()));
    }
    
    @Override
    public String getName() {
        return "wctime";
    }
    
    @Override
    public Class<?> getTargetType() {
        return Simulation.class;
    }
}
