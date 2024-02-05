package the.oronco.adt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import the.oronco.Rusty;

// TODO examples like in the rust documentation
// TODO replace exceptions with better exceptions
// TODO tests
public sealed interface Result<T, E> extends Rusty<Optional<T>> {
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    final class Ok<T, E> implements Result<T, E> {
        private final T result;

        public T result() {return result;}
    }

    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    final class Err<T, E> implements Result<T, E> {
        private final E error;

        public E error() {return error;}
    }

    default boolean isOk() {
        return switch (this) {
            case Ok<T, E> ignored:
                yield true;
            case Err<T, E> ignored:
                yield false;
        };
    }

    /**
     * Returns {@code true} if the result is {@code Ok<T,E>}.
     *
     * @param predicate condition that an {@code Ok<T,E>} result should conform to
     *
     * @return if the result is {@code Ok<T,E>} and conforms to the given predicate
     */
    default boolean isOkAnd(Predicate<? super T> predicate) {
        return switch (this) {
            case Ok<T, E> ok:
                yield predicate.test(ok.result);
            case Err<T, E> ignored:
                yield false;
        };
    }

    default boolean isErr() {
        return switch (this) {
            case Ok<T, E> ignored:
                yield false;
            case Err<T, E> ignored:
                yield true;
        };
    }

    /**
     * Returns {@code true} if the result is {@code Err<T,E>}.
     *
     * @param predicate condition that an {@code Err<T,E>} error value should conform to
     *
     * @return if the result is {@code Err<T,E>} and conforms to the given predicate
     */
    default boolean isErrAnd(Predicate<? super E> predicate) {
        return switch (this) {
            case Ok<T, E> ignored:
                yield false;
            case Err<T, E> err:
                yield predicate.test(err.error);
        };
    }

    /**
     * Converts from {@code Result<T, E>} to {@code Option<T>} discarding the error.
     *
     * @return an {@code Option<T>} representing the {@code Ok<T, E>} if any
     */
    default Option<T> ok() {
        return switch (this) {
            case Ok<T, E> ok:
                yield Option.some(ok.result);
            case Err<T, E> ignored:
                yield Option.none();
        };
    }

    /**
     * Converts from {@code Result<T, E>} to {@code Option<E>} discarding the result.
     *
     * @return an {@code Option<E>} representing the {@code Err<T, E>} if any
     */
    default Option<E> err() {
        return switch (this) {
            case Ok<T, E> ignored:
                yield Option.none();
            case Err<T, E> err:
                yield Option.some(err.error);
        };
    }

    /**
     * Maps a {@code Result<T, E>} to {@code Result<U, E>} by applying a function to a contained {@code Ok<T, E>} value, leaving an
     * {@code Err<T, E>} value untouched.
     * <p>
     * This function can be used to compose the results of two functions.
     *
     * @param f   function that can convert {@code T} to {@code U}
     * @param <U> result type of the conversion if {@code this} result was {@code Ok<T,E>}
     *
     * @return a new result with a converted value
     */
    default <U> Result<U, E> map(Function<? super T, ? extends U> f) {
        return switch (this) {
            case Ok<T, E> ok:
                yield ok(f.apply(ok.result));
            case Err<T, E> err:
                yield err(err.error);
        };
    }

    /**
     * Returns the provided default (if {@code Err<T,E>}), or applies a function to the contained value (if {@code Ok<T, E>}),
     * <p>
     * Arguments passed to {@link Result#mapOr(Object, Function)} are eagerly evaluated; if you are passing the result of a function call,
     * it is recommended to use {@link Result#mapOrElse(Function, Function)}, which is lazily evaluated.
     *
     * @param f   function that can convert {@code T} to {@code U}
     * @param <U> result type of the conversion if {@code this} result was {@code Ok<T,E>}
     *
     * @return a new result with a converted value
     */
    default <U> U mapOr(U defaultValue, Function<? super T, ? extends U> f) {
        return switch (this) {
            case Ok<T, E> ok:
                yield f.apply(ok.result);
            case Err<T, E> ignored:
                yield defaultValue;
        };
    }

    /**
     * Maps a {@code Result<T, E>} to {@code U} by applying fallback function {@code d} to a contained {@code Err<T, E>} value, or function
     * {@code f} to a contained {@code Ok<T, E>} value.
     * <p>
     * This function can be used to unpack a successful result while handling an error.
     *
     * @param d   function that provides a default given an error
     * @param f   function mapping the value of a successful result
     * @param <U> type returned no matter if the result was {@code Ok<T, E>} or {@code Err<T, E>}
     *
     * @return the mapped result or a default value
     */
    default <U> U mapOrElse(Function<? super E, ? extends U> d, Function<? super T, ? extends U> f) {
        return switch (this) {
            case Ok<T, E> ok:
                yield f.apply(ok.result);
            case Err<T, E> err:
                yield d.apply(err.error);
        };
    }

    /**
     * Maps a {@code Result<T, E>} to {@code Result<T, F>} by applying a function to a contained {@code Err<T, E>} value, leaving an
     * {@code Ok<T, E>} value untouched.
     * <p>
     * This function can be used to pass through a successful result while handling an error.
     *
     * @param f   function that converts the error into a more usable form (e.g. http status code to a message for the user)
     * @param <U> type of the new error
     *
     * @return a result containing either the unchanged successful result or a converted error
     */
    default <U> Result<T, U> mapErr(Function<? super E, ? extends U> f) {
        return switch (this) {
            case Ok<T, E> ok:
                yield ok(ok.result);
            case Err<T, E> err:
                yield err(f.apply(err.error));
        };
    }


    /**
     * Calls the provided {@code Consumer<T, E>} with a reference to the contained value (if {@code Ok<T, E>}).
     * <p>
     * This can be used to chain multiple consumptions of a successful result without unwrapping the result.
     *
     * @param f consumer that wants the value
     *
     * @return the result it was called on
     */
    default Result<T, E> inspect(Consumer<? super T> f) {
        if (this instanceof Ok<T, E> ok) {
            f.accept(ok.result);
        }
        return this;
    }

    /**
     * Calls the provided {@code Consumer<T, E>} with a reference to the contained error (if {@code Err<T, E>}).
     * <p>
     * This can be used to chain multiple consumptions for handling an error without unwrapping the result.
     *
     * @param f consumer that wants the error
     *
     * @return the result it was called on
     */
    default Result<T, E> inspectErr(Consumer<? super E> f) {
        if (this instanceof Err<T, E> err) {
            f.accept(err.error);
        }
        return this;
    }

    default Iterator<T> iter() {
        return switch (this) {
            case Ok<T, E> ok:
                yield Stream.of(ok.result)
                            .iterator();
            case Err<T, E> ignored:
                yield new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public T next() {
                        return null;
                    }
                };
        };
    }

    /**
     * Returns the contained {@code Ok<T, E>} value.
     * <p>
     * Because this function may throw, its use is generally discouraged. Instead, prefer to use pattern matching and handle the
     * {@code Err<T, E>} case explicitly, or call {@link Result#unwrapOr(Object)} or {@link Result#unwrapOrElse(Supplier)}.
     *
     * @param message the message of the thrown error
     *
     * @return the successful result
     *
     * @throws NoSuchElementException when {@code this} is an {@code Err<T, E>}
     */
    default T expect(String message) throws NoSuchElementException {
        return switch (this) {
            case Ok<T, E> ok:
                yield ok.result;
            case Err<T, E> ignored:
                throw new NoSuchElementException(message);
        };
    }

    /**
     * Returns the contained {@code Ok<T, E>} value.
     * <p>
     * Because this function may throw, its use is generally discouraged. Instead, prefer to use pattern matching and handle the
     * {@code Err<T, E>} case explicitly, or call {@link Result#unwrapOr(Object)} or {@link Result#unwrapOrElse(Supplier)}.
     *
     * @return the successful result
     *
     * @throws NoSuchElementException when {@code this} is an {@code Err<T, E>}; the message is provided by the {@code Err<T, E>}s value
     */
    default T unwrap() throws NoSuchElementException {
        return switch (this) {
            case Ok<T, E> ok:
                yield ok.result;
            case Err<T, E> err:
                throw new NoSuchElementException("Result was unwrapped but it was Err: %s".formatted(err.error));
        };
    }

    /**
     * Returns the contained {@code Ok<T, E>} value or a default
     * <p>
     * Consumes the self argument then, if {@code Ok<T, E>}, returns the contained value, otherwise if {@code Err<T, E>}, returns the
     * provided {@code defaultValue}.
     *
     * @param defaultValue default value when {@code Err<T, E>}
     *
     * @return either the successful result value or the default value
     */
    default T unwrapOr(T defaultValue) {
        return switch (this) {
            case Ok<T, E> ok:
                yield ok.result;
            case Err<T, E> ignored:
                yield defaultValue;
        };
    }

    /**
     * Returns the contained {@code Ok<T, E>} value or a default provided by the {@code defaultSupplier}
     * <p>
     * Consumes the self argument then, if {@code Ok<T, E>}, returns the contained value, otherwise if {@code Err<T, E>}, returns the
     * default value returned by the {@code defaultSupplier}.
     *
     * @param defaultSupplier default value when {@code Err<T, E>}
     *
     * @return either the successful result value or the default value provided by the {@code defaultSupplier}
     */

    default T unwrapOrElse(Supplier<? extends T> defaultSupplier) {
        return switch (this) {
            case Ok<T, E> ok:
                yield ok.result;
            case Err<T, E> ignored:
                yield defaultSupplier.get();
        };
    }

    /**
     * Returns the contained {@code Err<T, E>} value.
     *
     * @param message the message of the thrown error
     *
     * @return the successful result
     *
     * @throws NoSuchElementException when {@code this} is an {@code Ok<T, E>}
     */
    default E expectErr(String message) throws NoSuchElementException {
        return switch (this) {
            case Ok<T, E> ignored:
                throw new NoSuchElementException(message);
            case Err<T, E> err:
                yield err.error;
        };
    }

    /**
     * Returns the contained {@code Err<T, E>} value.
     *
     * @return the successful result
     *
     * @throws NoSuchElementException when {@code this} is an {@code Ok<T, E>}; the message is provided by the {@code Ok<T, E>}s value
     */
    default E unwrapErr() throws NoSuchElementException {
        return switch (this) {
            case Ok<T, E> ok:
                throw new NoSuchElementException("Result was unwrapped but it was Ok: %s".formatted(ok.result));
            case Err<T, E> err:
                yield err.error;
        };
    }

    /**
     * Returns {@code other} if the result is {@code Ok<T, E>}, otherwise returns the {@code Err<T, E>} value of {@code this}.
     * <p>
     * Arguments passed to {@code and()} are eagerly evaluated; if you are passing the result of a function call, it is recommended to use
     * {@link Result#andThen(Supplier)}, which is lazily evaluated.
     *
     * @param other another result which should be conditionally be returned depending on the success status of {@code this}
     * @param <U>   type of the other successful result
     *
     * @return the other successful result or the error of {@code this}
     */
    default <U> Result<U, E> and(Result<U, E> other) {
        return switch (this) {
            case Ok<T, E> ok:
                yield other;
            case Err<T, E> err:
                yield err(err.error);
        };
    }

    /**
     * Calls {@code f} if the result is {@code Ok<T, E>}, otherwise returns the {@code Err<T, E>} value of {@code this}.
     * <p>
     * This function can be used for control flow based on Result values.
     *
     * @param f   supplier of another result which should be conditionally be returned depending on the success status of {@code this}
     * @param <U> type of the other successful result
     *
     * @return the other successful result or the error of {@code this}
     */
    default <U> Result<? extends U, ? extends E> andThen(Supplier<Result<? extends U, ? extends E>> f) {
        return switch (this) {
            case Ok<T, E> ignored:
                yield f.get();
            case Err<T, E> err:
                yield err(err.error);
        };
    }

    /**
     * Returns {@code other} if the result is {@code Err<T, F>}, otherwise returns the {@code Ok<T, F>} value of {@code this}.
     * <p>
     * Arguments passed to {@code or()} are eagerly evaluated; if you are passing the result of a function call, it is recommended to use
     * {@link Result#orThen(Supplier)}, which is lazily evaluated.
     *
     * @param other the fallback result in case {@code this} is an {@code Err<T, E>}
     * @param <F>   type of the error of the other result
     *
     * @return {@code this} if it is successful {@code other} otherwise
     */
    default <F> Result<T, F> or(Result<T, F> other) {
        return switch (this) {
            case Ok<T, E> ok:
                yield ok(ok.result);
            case Err<T, E> ignored:
                yield other;
        };
    }

    /**
     * Calls f if the result is {@code Err<T, E>}, otherwise returns the {@code Ok<T, E>} value of {@code this}.
     * <p>
     * This function can be used for control flow based on result values.
     *
     * @param f   provider of the fallback result in case {@code this} is an {@code Err<T, E>}
     * @param <F> type of the error of the other result
     *
     * @return {@code this} if it is successful {@code other} otherwise
     */
    default <F> Result<? extends T, ? extends F> orThen(Supplier<Result<? extends T, ? extends F>> f) {
        return switch (this) {
            case Ok<T, E> ok:
                yield ok(ok.result);
            case Err<T, E> ignored:
                yield f.get();
        };
    }

    @Override
    default Optional<T> j() {
        return Optional.ofNullable(this.unwrapOr(null));
    }

    static <T, E> Err<T, E> err(E error) {
        return new Err<>(error);
    }

    static <T, E> Ok<T, E> ok(T result) {
        return new Ok<>(result);
    }
}
