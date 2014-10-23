package ch.bfh.ti.jts.console;

import java.util.Collection;
import java.util.LinkedList;

import ch.bfh.ti.jts.console.commands.Command;
import ch.bfh.ti.jts.console.commands.SimulationCommand;
import ch.bfh.ti.jts.console.commands.TimeCommand;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class JtsConsole extends BasicConsole {
    
    public class MainParams {
        
        @Parameter(names = { "--help", "--h" }, description = "Help")
        private boolean help = false;
    }
    
    private JCommander                jc;
    private MainParams                mainParams = new MainParams();
    private final Collection<Command> commands   = new LinkedList<>();
    
    public JtsConsole() {
        buildCommands();
    }
    
    private void buildCommands() {
        
        jc = new JCommander(mainParams);
        
        // do this with reflection later...
        
        commands.add(new SimulationCommand());
        commands.add(new TimeCommand());
        
        commands.forEach(command -> {
            String name = command.getName();
            jc.addCommand(name, command.getParameters());
        });
    }
    
    @Override
    protected void parseCommand(String line) {
        if (line != null && !"".equals(line)) {
            try {
                String[] args = line.split(" ");
                
                buildCommands();
                jc.parse(args);
                
                String commandName = jc.getParsedCommand();
                if (commandName != null) {
                    Command command = commands.stream().filter(x -> x.getName().equals(commandName)).findFirst().orElse(null);
                    if (command != null) {
                        command.execute(this, jc, getNet());
                    }
                } else {
                    // display help...
                    if (mainParams.help) {
                        StringBuilder sb = new StringBuilder();
                        jc.usage(sb);
                        writeLine(sb.toString());
                    }
                }
                
            } catch (ParameterException ex) {
                writeLine(ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
