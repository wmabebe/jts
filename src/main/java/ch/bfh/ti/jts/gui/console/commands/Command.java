package ch.bfh.ti.jts.gui.console.commands;

/**
 * Interface describing available console commands.
 *
 * @author Mathias
 */
public interface Command {

    /**
     * Executes the command.
     *
     * @param executor
     *            the object on which to execute.
     * @return console output
     */
    String execute(Object executor);

    /**
     * Gets the name of the command.
     *
     * @return name of the command
     */
    String getName();

    /**
     * The type of classes for which this command is intended
     *
     * @return
     */
    Class<?> getTargetType();

}
