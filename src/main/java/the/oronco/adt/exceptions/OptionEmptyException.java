package the.oronco.adt.exceptions;

/**
 * @author the_oronco@posteo.net
 * @since 10/03/2024
 */
public class OptionEmptyException extends Exception {
    public OptionEmptyException(String message) {
        super(message);
    }
}
