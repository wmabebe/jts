package ch.bfh.ti.jts.gui.console;

import ch.bfh.ti.jts.gui.Renderable;

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
     * Write a line to the console which is not being executed (just output).
     *
     * @param line
     *            line to be printed
     */
    void write(final String line);
}
