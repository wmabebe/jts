package ch.bfh.ti.jts.console.commands;

import ch.bfh.ti.jts.console.Console;
import ch.bfh.ti.jts.data.Net;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class SimulationCommand implements Command {
    
    public class SimulationCommandParams {
        
        @Parameter(names = { "--help", "--h" }, description = "Help")
        private boolean help = false;
    }
    
    private SimulationCommandParams params = new SimulationCommandParams();
    
    @Override
    public String getName() {
        return "simulation";
    }
    
    @Override
    public Object getParameters() {
        return params;
    }
    
    @Override
    public void execute(Console console, JCommander jc, Net net) {
        console.writeLine("do simulation stuff here...");
    }
}
