package the.oronco.adt.funcs;

import the.oronco.adt.Result;
import the.oronco.adt.exceptions.WrongKindOfExceptionError;

import java.util.function.BiFunction;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, X extends Exception> extends BiFunction<T, U, Result<R,X>> {
    R applyThrowing(T t, U u) throws X;

    @Override
    default Result<R, X> apply(T t, U u) throws WrongKindOfExceptionError {
        try {
            return Result.ok(applyThrowing(t, u));
        } catch (Exception thrownException) {
            try {
                @SuppressWarnings("unchecked")
                X exception = (X) thrownException;
                return Result.err(exception);
            } catch (ClassCastException castException) {
                throw new WrongKindOfExceptionError(
                        (
                                "The Exception that was thrown (%s) by the Function did not match the Exception that "
                                + "was expected to be thrown by it. Either go up a few levels in the Exception "
                                + "inheritance hierarchy or fix your method calls!").formatted(thrownException.getClass()
                                                                                                              .getSimpleName()),
                        thrownException);
            }
        }
    }
}
