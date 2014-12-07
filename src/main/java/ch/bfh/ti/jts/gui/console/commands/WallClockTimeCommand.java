package ch.bfh.ti.jts.gui.console.commands;

import ch.bfh.ti.jts.simulation.Simulation;

public class WallClockTimeCommand implements Command {
    
    @Override
    public String execute(final Object executor) {
        final Simulation simulation = (Simulation) executor;
        return String.format("Wall clock time: %.5f seconds", simulation.getWallClockTime());
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
