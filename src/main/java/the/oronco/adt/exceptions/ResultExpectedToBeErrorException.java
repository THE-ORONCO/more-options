package the.oronco.adt.exceptions;

/**
 * @author the_oronco@posteo.net
 * @since 10/03/2024
 */
public class ResultExpectedToBeErrorException extends Exception {
    public ResultExpectedToBeErrorException(String message) {
        super(message);
    }
}
