package the.oronco.adt.exceptions;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
public class WrongKindOfExceptionError extends RuntimeException {
    public WrongKindOfExceptionError(String message, Throwable cause) {
        super(message, cause);
    }
}
