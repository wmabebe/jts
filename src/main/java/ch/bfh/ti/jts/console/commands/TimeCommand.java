package ch.bfh.ti.jts.console.commands;

import ch.bfh.ti.jts.console.Console;
import ch.bfh.ti.jts.data.Net;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class TimeCommand implements Command {
    
    public class TimeCommandParams {
        
        @Parameter(names = { "--help", "--h" }, description = "Help")
        private boolean help = false;
    }
    
    private TimeCommandParams params = new TimeCommandParams();
    
    @Override
    public String getName() {
        return "time";
    }
    
    @Override
    public Object getParameters() {
        return params;
    }
    
    @Override
    public void execute(Console console, JCommander jc, Net net) {
        console.writeLine("do time stuff here...");
    }    
}
