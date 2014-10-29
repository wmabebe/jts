package ch.bfh.ti.jts.console.commands;

import ch.bfh.ti.jts.data.Net;

public class TimeCommand implements Command {
    
    @Override
    public String execute(final Object executor) {
        final Net net = (Net) executor;
        return String.format("time: %.2f seconds", net.getTimeTotal());
    }
    
    @Override
    public String getName() {
        return "time";
    }
    
    @Override
    public Class<?> getTargetType() {
        return Net.class;
    }
    
}
