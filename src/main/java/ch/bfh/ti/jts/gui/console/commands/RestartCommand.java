package ch.bfh.ti.jts.gui.console.commands;

import java.util.Optional;

import ch.bfh.ti.jts.App;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Restart the simulation")
public class RestartCommand extends Command {
    
    @Override
    public Optional<String> execute(final Object executor) {
        final App app = (App) executor;
        if (app == null) {
            throw new IllegalArgumentException("app");
        }
        app.restart();
        return Optional.of("simulation restarted");
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
