package the.oronco.adt.exceptions;

import lombok.Getter;

/**
 * Should be raised, when a value is not valid for a given bound. Used in the {@link the.oronco.adt.Condition} Type.
 **/
@Getter
public class ValueNotValidForBoundError extends RuntimeException {

    public ValueNotValidForBoundError(String message) {
        super(message);
    }
}
