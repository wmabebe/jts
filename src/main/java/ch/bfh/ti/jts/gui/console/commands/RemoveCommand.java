package ch.bfh.ti.jts.gui.console.commands;

import java.util.Optional;

import ch.bfh.ti.jts.data.Net;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Remove agents")
public class RemoveCommand extends Command {
    
    @Parameter(names = { "-id", "id" }, description = "Id of the agent", required = true)
    private final int id = 1;
    
    @Override
    public Optional<String> execute(final Object executor) {
        final Net net = (Net) executor;
        boolean success = net.removeAgent(id);
        Optional<String> message = Optional.empty();
        if (success) {
            message = Optional.of("Agent removed");
        } else {
            message = Optional.of(String.format("No agent with id %d found", id));
        }
        return message;
    }
    
    @Override
    public String getName() {
        return "remove";
    }
    
    @Override
    public Class<?> getTargetType() {
        return Net.class;
    }
}
