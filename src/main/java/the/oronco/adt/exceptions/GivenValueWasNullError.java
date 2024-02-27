package the.oronco.adt.exceptions;

/**
 * If a given Value is null this error should be raised. Used in the {@link the.oronco.adt.Option} Type.
 **/
public class GivenValueWasNullError extends Exception {
    public GivenValueWasNullError(String message) {
        super(message);
    }
}
