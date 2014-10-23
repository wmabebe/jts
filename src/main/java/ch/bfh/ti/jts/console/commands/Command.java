package ch.bfh.ti.jts.console.commands;

import ch.bfh.ti.jts.console.Console;
import ch.bfh.ti.jts.data.Net;

import com.beust.jcommander.JCommander;

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
    public String getName();
    
    public Object getParameters();
    
    /**
     * Executes the command.
     * 
     * @param jc
     *            JCommander objects with arguments
     * @param net
     *            the net object
     */
    public void execute(Console console, JCommander jc, Net net);
}
