package ch.bfh.ti.jts.gui.console.commands;

import com.beust.jcommander.Parameters;

import ch.bfh.ti.jts.App;

@Parameters(commandDescription = "Restart the simulation")
public class RestartCommand extends Command {
    
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
