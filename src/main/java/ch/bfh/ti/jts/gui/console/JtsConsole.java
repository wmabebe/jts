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

/**
 * Console for the JavaTrafficSimulator.
 *
 * @author Enteee
 * @author winki
 */
public class JtsConsole extends BasicConsole {
    
    private static final Logger log = LogManager.getLogger(JtsConsole.class);
    
    public class MainParams {
        
        @Parameter(names = { "-help", "-h" }, description = "Help")
        private boolean help = false;
    }
    
    private final Reflections   reflections = new Reflections(Command.class.getPackage().getName());
    private final MainParams    mainParams  = new MainParams();
    private Collection<Command> commands    = new LinkedList<>();
    private JCommander          jc          = new JCommander(mainParams);
    
    public JtsConsole() {
        reloadCommands();
    }
    
    private void reloadCommands() {
        jc = new JCommander(mainParams);
        commands = new LinkedList<>();
        reflections.getSubTypesOf(Command.class).forEach((commandClass) -> {
            try {
                Command command = commandClass.newInstance();
                commands.add(command);
                jc.addCommand(command.getName(), command);
                log.info(String.format("Loaded command %s", command.getName()));
            } catch (Exception e) {
                log.fatal("Failed instantiating command " + commandClass, e);
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
                } else {
                    final String commandName = jc.getParsedCommand();
                    if (commandName != null) {
                        commands.stream().filter(x -> x.getName().equals(commandName)).findFirst().ifPresent(command -> {
                            if (command.help) {
                                showHelptext(commandName);
                                command.help = false; // reset value
                            } else {
                                execute(command);
                            }
                        });
                    }
                }
            } catch (final ParameterException e) {
                write("Invalid command");
                log.warn("Invalid command: '" + line + "'", e);
            }
        }
        reloadCommands();
    }
}
