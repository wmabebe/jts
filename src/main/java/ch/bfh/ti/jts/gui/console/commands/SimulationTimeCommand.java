package ch.bfh.ti.jts.gui.console.commands;

import com.beust.jcommander.Parameters;

import ch.bfh.ti.jts.data.Net;

@Parameters(commandDescription = "Show simulation time")
public class SimulationTimeCommand extends Command {
    
    @Override
    public String execute(final Object executor) {
        final Net net = (Net) executor;
        return String.format("Simulation time: %.5f seconds", net.getSimulationTime());
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
