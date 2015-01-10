package ch.bfh.ti.jts.exceptions;

/**
 * Exceptions that is thrown, when an argument is null.
 *
 * @author Enteee
 * @author winki
 */
public class ArgumentNullException extends IllegalArgumentException {
    
    private static final long serialVersionUID = 1L;
    
    public ArgumentNullException(String variable) {
        super(String.format("%s is null", variable));
    }
}
