package ch.bfh.ti.jts.utils.exceptions;

import ch.bfh.ti.jts.data.Agent;

/**
 * Thrown when a {@linke Agent} gets inserted in a @{link Lane} at a relative
 * position where already an other {@link Agent} exists.
 * 
 * @author ente
 */
public class PositionOccupiedException extends Exception {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
}
