package ch.bfh.ti.jts.gui.console;

import ch.bfh.ti.jts.gui.Renderable;

/**
 * Interface for GUI consoles.
 *
 * @author Enteee
 * @author winki
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
     * String is typed on the console.
     *
     * @param string
     *            typed string
     */
    void stringTyped(final String string);
    
    /**
     * Write a line to the console which is not being executed (just output).
     *
     * @param line
     *            line to be printed
     */
    void write(final String line);
}
