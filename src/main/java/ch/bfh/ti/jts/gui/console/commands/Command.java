package ch.bfh.ti.jts.gui.console.commands;

import com.beust.jcommander.Parameter;

/**
 * Interface describing available console commands.
 *
 * @author Enteee
 * @author winki
 */
public abstract class Command {
    
    @Parameter(names = { "-help", "-h" }, description = "Help")
    public boolean help = false;
    
    /**
     * Executes the command.
     *
     * @param executor
     *            the object on which to execute.
     * @return console output
     */
    public abstract String execute(Object executor);
    
    /**
     * Gets the name of the command.
     *
     * @return name of the command
     */
    public abstract String getName();
    
    /**
     * The type of classes for which this command is intended
     *
     * @return
     */
    public abstract Class<?> getTargetType();
}
