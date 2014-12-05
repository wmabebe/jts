package ch.bfh.ti.jts.gui.console.commands;

import ch.bfh.ti.jts.gui.console.JtsConsole;

public class ToggleInterpolate implements Command {
    
    @Override
    public String execute(Object executor) {
        final JtsConsole console = (JtsConsole) executor;
        return "";
    }
    
    @Override
    public String getName() {
        return "toggleInterpolate";
    }
    
    @Override
    public Class<?> getTargetType() {
        return JtsConsole.class;
    }
    
}
