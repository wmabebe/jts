package ch.bfh.ti.jts.console;

import ch.bfh.ti.jts.gui.Renderable;
import ch.bfh.ti.jts.simulation.Simulation;

/**
 * Interface for the gui console.
 *
 * @author Mathias
 */
public interface Console extends Renderable {
    
    /**
     * Write a line to the console which will be executed.
     *
     * @param line
     *            line to be executed
     */
    void executeCommand(final String line);
    
    /**
     * Key is typed on the console.
     *
     * @param character
     *            typed character
     */
    void keyTyped(final char character);
    
    /**
     * Set the net where the console can execute commands on.
     *
     * @param net
     *            data object
     */
    void setSimulation(final Simulation simulation);
    
    /**
     * Write a line to the console which is not being executed (just output).
     *
     * @param line
     *            line to be printed
     */
    void write(final String line);
}
