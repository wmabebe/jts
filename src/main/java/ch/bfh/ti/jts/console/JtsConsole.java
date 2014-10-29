package ch.bfh.ti.jts.console;

import java.util.Collection;
import java.util.LinkedList;

import ch.bfh.ti.jts.console.commands.Command;
import ch.bfh.ti.jts.console.commands.SpawnCommand;
import ch.bfh.ti.jts.console.commands.TimeCommand;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class JtsConsole extends BasicConsole {
    
    public class MainParams {
        
        @Parameter(names = { "-help", "-h" }, description = "Help")
        private final boolean help = false;
    }
    
    private JCommander                jc;
    private final MainParams          mainParams = new MainParams();
    private final Collection<Command> commands   = new LinkedList<>();
    
    private JCommander buildCommander() {
        
        JCommander jcommander = new JCommander(mainParams);
        
        // TODO: do this with reflection?
        commands.clear();
        commands.add(new TimeCommand());
        commands.add(new SpawnCommand());
        
        commands.forEach(command -> {
            String name = command.getName();
            jcommander.addCommand(name, command);
        });
        
        return jcommander;
    }
    
    @Override
    protected void parseCommand(String line) {
        if (line != null && !"".equals(line)) {
            try {
                String[] args = line.split(" ");
                
                jc = buildCommander();
                jc.parse(args);
                
                String commandName = jc.getParsedCommand();
                if (commandName != null) {
                    Command command = commands.stream().filter(x -> x.getName().equals(commandName)).findFirst().orElse(null);
                    if (command != null) {
                        execute(command);
                    }
                } else {
                    // display help...
                    if (mainParams.help) {
                        StringBuilder sb = new StringBuilder();
                        jc.usage(sb);
                        write(sb.toString());
                    }
                }
                
            } catch (ParameterException ex) {
                write(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    private void execute(final Command command) {
        getSimulation().addCommand(command);
    }
}
