package the.oronco.adt.funcs;

import the.oronco.adt.Result;
import the.oronco.adt.exceptions.WrongKindOfExceptionError;

import java.util.function.Function;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
@FunctionalInterface
public interface ThrowingFunction<T, R, X extends Exception> extends Function<T, Result<R, X>> {
    R applyThrowing(T input) throws X;


    /**
     * Default {@link Function#apply(Object)} that wraps any thrown checked exceptions (by default in a {@link RuntimeException}).
     *
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    @Override
    default Result<R, X> apply(T t) throws WrongKindOfExceptionError {
        try {
            return Result.ok(applyThrowing(t));
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

    /**
     * Lambda friendly convenience method that can be used to create a {@link ThrowingFunction} where the {@link #apply(Object)} method wraps any
     * checked exception thrown by the supplied lambda expression or method reference.
     * <p>This method can be especially useful when working with method references.
     * It allows you to easily convert a method that throws a checked exception into an instance compatible with a regular {@link Function}.
     * <p>For example:
     * <pre class="code">
     * stream.map(ThrowingFunction.of(Example::methodThatCanThrowCheckedException));
     * </pre>
     *
     * @param <T>      the type of the input to the function
     * @param <R>      the type of the result of the function
     * @param <X>      the type of the thrown exception
     * @param function the source function
     * @return a new {@link ThrowingFunction} instance
     */
    static <T, R, X extends Exception> ThrowingFunction<T, R, X> of(ThrowingFunction<T, R, X> function) {
        return function;
    }
}
