package ch.bfh.ti.jts.gui.console.commands;

import com.beust.jcommander.Parameter;

/**
 * Describing all console commands.
 *
 * @author Enteee
 * @author winki
 */
public abstract class Command {
    
    @Parameter(names = { "-help", "-h" }, description = "Help")
    public boolean help = false;
    
    /**
     * Gets the name of the command.
     *
     * @return name of the command
     */
    public abstract String getName();
    
    /**
     * Executes the command.
     *
     * @param executor
     *            the object on which to execute.
     * @return console output
     */
    public abstract String execute(Object executor);
    
    /**
     * The type of classes this command will be sent to.
     *
     * @return
     */
    public abstract Class<?> getTargetType();
}
