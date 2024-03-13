package the.oronco.adt.exceptions;

/**
 * @author the_oronco@posteo.net
 * @since 10/03/2024
 */
public class ConditionDoesNotHoldException extends Exception {
    public ConditionDoesNotHoldException(String message) {
        super(message);
    }
}
