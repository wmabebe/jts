package ch.bfh.ti.jts.console.commands;

import ch.bfh.ti.jts.simulation.Simulation;

public class TimeCommand implements Command {
    
    @Override
    public String getName() {
        return "time";
    }
    
    @Override
    public String execute(Simulation simulation) {
        return String.format("time: %.2f seconds", simulation.getTimeTotal());
    }
}
