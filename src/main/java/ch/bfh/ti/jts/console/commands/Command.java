package ch.bfh.ti.jts.console.commands;

import ch.bfh.ti.jts.simulation.Simulation;

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
     * Executes the command.
     * 
     * @param jc
     *            JCommander objects with arguments
     * @param simulation
     *            the simulation object
     * @return console output
     */
    String execute(Simulation simulation);
}
