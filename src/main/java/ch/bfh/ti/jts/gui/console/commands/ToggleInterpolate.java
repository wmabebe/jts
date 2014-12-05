package ch.bfh.ti.jts.gui.console.commands;

import ch.bfh.ti.jts.simulation.Simulation;

public class ToggleInterpolate implements Command {
    
    @Override
    public String execute(Object executor) {
        final Simulation simulation = (Simulation) executor;
        simulation.toggleInterpolateWallClockState();
        return "toggled";
    }
    
    @Override
    public String getName() {
        return "toggleInterpolate";
    }
    
    @Override
    public Class<?> getTargetType() {
        return Simulation.class;
    }
    
}
