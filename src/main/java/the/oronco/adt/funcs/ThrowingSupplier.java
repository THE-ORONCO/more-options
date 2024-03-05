package the.oronco.adt.funcs;

import lombok.SneakyThrows;
import org.springframework.data.domain.Example;
import the.oronco.adt.Result;
import the.oronco.adt.exceptions.WrongKindOfExceptionError;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
@FunctionalInterface
public interface ThrowingSupplier<R,X extends Exception> extends Function<Void, Result<R, X>>, Supplier<R> {

    R getThrows() throws X;
    @Override
    @SneakyThrows
    default R get(){
        return getThrows();
    }

    @Override
    default Result<R, X> apply(Void ignored){
        try {
            return Result.ok(getThrows());
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
     * {@link ThrowingSupplier} where the {@link #get()} method wraps any checked
     * exception thrown by the supplied lambda expression or method reference.
     * <p>This method can be especially useful when working with method references.
     * It allows you to easily convert a method that throws a checked exception
     * into an instance compatible with a regular {@link Supplier}.
     * <p>For example:
     * <pre class="code">
     * optional.orElseGet(ThrowingSupplier.of(Example::methodThatCanThrowCheckedException));
     * </pre>
     * @param <T> the type of results supplied by this supplier
     * @param <X> the type of the exception thrown by this supplier
     * @param supplier the source supplier
     * @return a new {@link ThrowingSupplier} instance
     */
    static <T, X extends Exception> ThrowingSupplier<T,X> of(ThrowingSupplier<T, X> supplier) {
        return supplier;
    }
}
