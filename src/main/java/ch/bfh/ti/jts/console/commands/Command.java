package ch.bfh.ti.jts.console.commands;

/**
 * Interface describing available console commands.
 * 
 * @author Mathias
 */
public interface Command {
    
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
    
    /**
     * Is this a broadcast command: Will be executed for all element of the
     * type: T.
     * 
     * @return
     */
    boolean isBroadcastCommand();
    
    /**
     * If not a broadcast command this method should return the target element
     * id.
     * 
     * @return
     */
    
    int getTargetElement();
    
    /**
     * Executes the command.
     * 
     * @param executor
     *            the object on which to execute.
     * @return console output
     */
    String execute(Object executor);
    
}
