package ch.bfh.ti.jts.gui.console;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import ch.bfh.ti.jts.App;
import ch.bfh.ti.jts.gui.console.commands.Command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class JtsConsole extends BasicConsole {
    
    public final static Logger LOG = LogManager.getLogger(JtsConsole.class);
    
    public class MainParams {
        
        @Parameter(names = { "-help", "-h" }, description = "Help")
        private final boolean help = false;
    }
    
    private final MainParams          mainParams = new MainParams();
    private final JCommander          jc         = new JCommander(mainParams);
    private final Collection<Command> commands   = new LinkedList<>();
    
    public JtsConsole() {
        
        // TODO: do this with reflection?
        Reflections reflections = new Reflections(Command.class.getPackage().getName());
        reflections.getSubTypesOf(Command.class).forEach((clazz) -> {
            try {
                Command command = clazz.newInstance();
                commands.add(command);
                jc.addCommand(command.getName(), command);
                LOG.info("Loaded command:" + command.getName());
            } catch (Exception e) {
                LOG.fatal("Failed instantiating:" + clazz, e);
            }
        });
    }
    
    private void execute(final Command command) {
        // forward command to App thread
        App.getInstance().addCommand(command);
    }
    
    @Override
    protected void parseCommand(final String line) {
        if (line != null && !"".equals(line)) {
            try {
                final String[] args = line.split(" ");
                
                jc.parse(args);
                
                final String commandName = jc.getParsedCommand();
                if (commandName != null) {
                    final Command command = commands.stream().filter(x -> x.getName().equals(commandName)).findFirst().orElse(null);
                    if (command != null) {
                        execute(command);
                    }
                } else {
                    // display help...
                    if (mainParams.help) {
                        final StringBuilder sb = new StringBuilder();
                        jc.usage(sb);
                        write(sb.toString());
                    }
                }
                
            } catch (final ParameterException e) {
                write("Invalid command");
                LOG.warn("Invalid command");
            }
        }
    }
}
