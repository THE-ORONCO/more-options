package the.oronco.adt.funcs;

import lombok.SneakyThrows;
import the.oronco.adt.Result;
import the.oronco.adt.Result.GOOD;
import the.oronco.adt.exceptions.WrongKindOfExceptionError;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
@FunctionalInterface
public interface ThrowingConsumer<T, X extends Exception> extends Function<T, Result<GOOD, X>>, Consumer<T> {
    void acceptThrows(T t) throws X;

    @Override
    @SneakyThrows
    default void accept(T t) {
        acceptThrows(t);
    }

    @Override
    default Result<GOOD, X> apply(T t){
        try {
            acceptThrows(t);
            return Result.good();
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
     * Lambda friendly convenience method that can be used to create a
     * {@link the.oronco.adt.funcs.ThrowingConsumer} where the {@link #acceptThrows(Object)} method wraps
     * any checked exception thrown by the supplied lambda expression or method
     * reference.
     * <p>This method can be especially useful when working with method references.
     * It allows you to easily convert a method that throws a checked exception
     * into an instance compatible with a regular {@link Consumer}.
     * <p>For example:
     * <pre class="code">
     * list.forEach(ThrowingConsumer.of(Example::methodThatCanThrowCheckedException));
     * </pre>
     * @param <T> the type of the input to the operation
     * @param <X> the type of the thrown exception
     * @param consumer the source consumer
     * @return a new {@link ThrowingConsumer} instance
     */
    static <T, X extends Exception> ThrowingConsumer<T,X> of(ThrowingConsumer<T, X> consumer) {
        return consumer;
    }
}
