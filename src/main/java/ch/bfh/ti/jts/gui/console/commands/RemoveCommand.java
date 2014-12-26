package ch.bfh.ti.jts.gui.console.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import ch.bfh.ti.jts.data.Net;

@Parameters(commandDescription = "Remove agents")
public class RemoveCommand extends Command {
    
    @Parameter(names = { "-id", "id" }, description = "Id of the agent", required = true)
    private int id = 1;
    
    @Override
    public String execute(final Object executor) {
        final Net net = (Net) executor;
        boolean success = net.removeAgent(id);
        if (success) {
            return "Agent removed";
        } else {
            return String.format("No agent with id %d found", id);
        }
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
