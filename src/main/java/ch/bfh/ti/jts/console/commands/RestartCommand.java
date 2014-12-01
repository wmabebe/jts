package ch.bfh.ti.jts.console.commands;

import ch.bfh.ti.jts.simulation.Simulation;

public class RestartCommand implements Command {
    
    @Override
    public String execute(final Object executor) {
        final Simulation simulation = (Simulation) executor;
        assert simulation != null;
        
        simulation.restart();
        
        return "simulation restarted.";
    }
    
    @Override
    public String getName() {
        return "restart";
    }
    
    @Override
    public Class<?> getTargetType() {
        return Simulation.class;
    }    
}
