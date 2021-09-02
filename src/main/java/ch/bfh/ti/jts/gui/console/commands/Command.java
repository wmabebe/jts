package ch.bfh.ti.jts.gui.console.commands;

import java.util.Optional;

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
     * Executes the command.
     *
     * @param executor
     *            the object on which to execute.
     * @return console output
     */
    public abstract Optional<String> execute(Object executor);

    /**
     * Gets the name of the command.
     *
     * @return name of the command
     */
    public abstract String getName();

    /**
     * The type of classes this command will be sent to.
     *
     * @return
     */
    public abstract Class<?> getTargetType();
}
