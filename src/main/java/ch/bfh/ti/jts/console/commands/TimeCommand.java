package ch.bfh.ti.jts.console.commands;

import ch.bfh.ti.jts.data.Net;

public class TimeCommand implements Command {
    
    @Override
    public String getName() {
        return "time";
    }
    
    @Override
    public boolean isBroadcastCommand() {
        return false;
    }
    
    @Override
    public int getTargetElement() {
        return 0;
    }
    
    @Override
    public Class<?> getTargetType() {
        return Net.class;
    }
    
    @Override
    public String execute(Object executor) {
        Net net = (Net) executor;
        return String.format("time: %.2f seconds", net.getTimeTotal());
    }
    
}
