package ch.bfh.ti.jts.console.commands;

import ch.bfh.ti.jts.App;

public class RestartCommand implements Command {
    
    @Override
    public String execute(final Object executor) {
        final App app = (App) executor;
        if (app == null) {
            throw new IllegalArgumentException("app");
        }
        app.restart();
        return "simulation restarted.";
    }
    
    @Override
    public String getName() {
        return "restart";
    }
    
    @Override
    public Class<?> getTargetType() {
        return App.class;
    }
}
