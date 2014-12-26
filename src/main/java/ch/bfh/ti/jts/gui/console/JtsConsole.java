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
        private boolean help = false;
    }
    
    private final MainParams          mainParams = new MainParams();
    private final JCommander          jc         = new JCommander(mainParams);
    private final Collection<Command> commands   = new LinkedList<>();
    
    public JtsConsole() {
        init();
    }
    
    private void init() {
        Reflections reflections = new Reflections(Command.class.getPackage().getName());
        reflections.getSubTypesOf(Command.class).forEach((clazz) -> {
            try {
                Command command = clazz.newInstance();
                commands.add(command);
                jc.addCommand(command.getName(), command);
                LOG.info(String.format("Loaded command %s", command.getName()));
            } catch (Exception e) {
                LOG.fatal("Failed instantiating command " + clazz, e);
            }
        });
    }
    
    private void execute(final Command command) {
        // forward command to App thread
        App.getInstance().addCommand(command);
    }
    
    private void showHelptext(String commandName) {
        final StringBuilder sb = new StringBuilder();
        if (commandName != null) {
            jc.usage(commandName, sb);
        } else {
            jc.usage(sb);
        }
        write(sb.toString());
    }
    
    @Override
    protected void parseCommand(final String line) {
        if (line != null && !"".equals(line)) {
            try {
                final String[] args = line.trim().split(" ");
                jc.parse(args);
                
                // display main help?
                if (mainParams.help) {
                    showHelptext(null);
                    mainParams.help = false; // reset value
                    return;
                }
                final String commandName = jc.getParsedCommand();
                if (commandName != null) {
                    final Command command = commands.stream().filter(x -> x.getName().equals(commandName)).findFirst().orElse(null);
                    if (command != null) {
                        if (command.help) {
                            showHelptext(commandName);
                            command.help = false; // reset value
                        } else {
                            execute(command);
                        }
                    }
                }
            } catch (final ParameterException e) {
                write("Invalid command");
                LOG.warn("Invalid command");
            }
        }
    }
}
