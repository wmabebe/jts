package ch.bfh.ti.jts.console.commands;

import ch.bfh.ti.jts.data.Net;

public class SimulationTimeCommand implements Command {
    
    @Override
    public String execute(final Object executor) {
        final Net net = (Net) executor;
        return String.format("Simulation time: %.2f", net.getSimulationTime());
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
