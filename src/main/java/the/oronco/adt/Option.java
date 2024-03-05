package the.oronco.adt;


import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.util.Streamable;
import the.oronco.Rusty;
import the.oronco.adt.exceptions.GivenValueWasNullError;
import the.oronco.adt.exceptions.WrongKindOfExceptionError;
import the.oronco.adt.funcs.ThrowingFunction;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

// TODO examples like in the rust documentation
// TODO replace exceptions with better exceptions
// TODO change naming scheme to be more java like (e.g. .unwrapOr -> .orElse, .unwrapOrElse ->
//  .orElseGet)
// TODO tests

/**
 * Similar to {@link java.util.Optional} in that it describes a value that can either be there or not. But
 * better as it can be used in the new switch pattern matching. It is inspired by the rust algebraic
 * type of <a href="https://doc.rust-lang.org/std/option/enum.Option.html">std::option::Option</a>.
 *
 * @param <T>
 */
@Unmodifiable
public sealed interface Option<T>
        extends Rusty<Optional<T>>, Try<T, Option<Infallible>>, Streamable<T>, Serializable {
    @MagicConstant
    None<?> NONE = new None<>();

    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Unmodifiable
    final class None<T> implements Option<@NotNull T> {
        @Contract(pure = true)
        @SuppressWarnings("unchecked")
        public <U> @NotNull Option<U> as() {
            return (Option<U>) NONE;
        }
    }

    @Unmodifiable
    record Some<T>(@NotNull @NonNull T value) implements Option<@NotNull T> {}

    @Contract(pure = true)
    default boolean isSome() {
        return switch (this) {
            case Some<T> ignored -> true;
            case None<T> ignored -> false;
        };
    }

    @Contract(pure = true)
    default boolean isNone() {
        return switch (this) {
            case Some<T> ignored -> false;
            case None<T> ignored -> true;
        };
    }

    /**
     * Returns true if the option is a Some and the value inside it matches a predicate.
     *
     * @param predicate predicate to evaluate the value against if the {@code Option<T>} is
     *                  {@code Some<T>}
     *
     * @return if the value exists && the predicate matches
     */
    default boolean isSomeAnd(@NotNull @NonNull Predicate<? super  @NotNull T> predicate) {
        return switch (this) {
            case Some<T>(T value) -> predicate.test(value);
            case None<T> ignored -> false;
        };
    }

    /**
     * Creates a {@link java.util.stream.Stream} from the {@code Option<T>} that contains the value of
     * {@code Some<T>} and is empty otherwise.
     *
     * @return the stream that contains the value of {@code Some<T>} and is empty otherwise
     */
    @Contract(pure = true)
    default @NotNull Stream<T> stream() {
        return switch (this) {
            case Some<T>(T value) -> Stream.of(value);
            case None<T> ignored -> Stream.of();
        };
    }

    /**
     * @param errorMessage the error message that is thrown when the {@code Option<T>} is
     *                     {@code None<T>}
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>}
     *
     * @throws java.util.NoSuchElementException with the given Error message when the {@code Option<T>} is
     *                                {@code None<T>}
     */
    default @NotNull T expect(String errorMessage) throws @NotNull NoSuchElementException {
        return switch (this) {
            case Some<T>(T value) -> value;
            case None<T> ignored -> throw new NoSuchElementException(errorMessage);
        };
    }

    /**
     * @param errorMessage the Exception that is thrown when the {@code Option<T>} is
     *                     {@code None<T>}
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>}
     *
     * @throws E with the given Exception when the {@code Option<T>} is {@code None<T>}
     */
    default @NotNull <E extends Exception> T expect(@NonNull  E errorMessage) throws @NotNull E {
        return switch (this) {
            case Some<T>(T value) -> value;
            case None<T> ignored -> throw errorMessage;
        };
    }

    /**
     * @param exceptionSupplier supplier that returns the Exception that is thrown when the
     *                          {@code Option<T>} is {@code None<T>}
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>}
     *
     * @throws E with the given Exception when the {@code Option<T>} is {@code None<T>}
     */
    default @NotNull <E extends Exception> T expectElse(@NotNull @NonNull  Supplier<@NotNull E> exceptionSupplier) throws
                                                                                                                  @NotNull E {
        return switch (this) {
            case Some<T>(T value) -> value;
            case None<T> ignored -> throw exceptionSupplier.get();
        };
    }

    /**
     * Returns the contained {@code Some<T>} value and throws otherwise. (Similar to
     * {@link Option#expect(String)} but without a custom error message)
     * <p>
     * The usage of this method is discouraged as control flow through exceptions can be hard to
     * understand and organize. Use {@link Option#unwrapOr(Object)} or
     * {@link Option#unwrapOrElse(java.util.function.Supplier)} instead.
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>}
     *
     * @throws java.util.NoSuchElementException when the {@code Option<T>} is {@code None<T>}
     */
    @Contract(pure = true)
    default @NotNull T unwrap() throws @NotNull NoSuchElementException {
        return switch (this) {
            case Some<T>(T value) -> value;
            case None<T> ignored ->
                    throw new NoSuchElementException("Option was unwrapped but it had no value!");
        };
    }

    /**
     * Returns the contained {@code Some<T>} value and a default value otherwise.<p> Arguments
     * passed to {@link Option#unwrapOr(T)} are eagerly evaluated; if you are passing the result of
     * a function call, it is recommended to use {@link Option#unwrapOrElse(java.util.function.Supplier)}, which is
     * lazily evaluated.
     *
     * @param defaultValue default value that should be returned in the case that {@code Option<T>}
     *                     is {@code None<T>}
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>} and the default value otherwise
     */
    @Contract(pure = true)
    default @Nullable T unwrapOr(@Nullable T defaultValue) {
        return switch (this) {
            case Some<T>(T value) -> value;
            case None<T> ignored -> defaultValue;
        };
    }

    /**
     * Returns the contained {@code Some<T>} value or computes it with the given
     * {@code Supplier<T>}.
     *
     * @param supplier the supplier that computes the value if {@code Option<T>} is {@code None<T>}
     *
     * @return the value in {@code Some<T>} or the result of the {@code Supplier<T>}
     */
    default @Nullable T unwrapOrElse(@NotNull @NonNull  Supplier<? extends @Nullable T> supplier) {
        return switch (this) {
            case Some<T>(T value) -> value;
            case None<T> ignored -> supplier.get();
        };
    }

    /**
     * Maps an {@code Option<T>} to {@code Option<R>} by applying a function to a contained value
     * (if Some) or returns None (if None).
     *
     * @param f   function that converts {@code T} to {@code R}
     * @param <R> type that the value of a {@code Some<T>} should be converted to
     *
     * @return a new {@code Option<R>} with the converted value
     */
    @Override
    default <R> @NotNull Option<R> map(@NotNull @NonNull  Function<? super @NotNull T, ? extends @NotNull R> f) {
        return switch (this) {
            case Some<T>(T value) -> some(f.apply(value));
            case None<T> ignored -> none();
        };
    }

    /**
     * Maps an {@code Option<T>} to {@code Option<R>} by applying a function to a contained value (if Some) or returns None (if None).
     *
     * @param f   function that converts {@code T} to {@code R}
     * @param <R> type that the value of a {@code Some<T>} should be converted to
     * @return a new {@code Option<R>} with the converted value
     */
    default <R, E extends Exception> @NotNull Option<R> safeMap(
            @NotNull @NonNull ThrowingFunction<? super @NotNull T, ? extends @NotNull R, ? extends @NotNull E> f) throws WrongKindOfExceptionError {
        return switch (this) {
            case Some<T>(T value) -> switch (f.apply(value)) {
                case Result.Ok<? extends R, ? extends E>(R result) -> some(result);
                case Result.Err<? extends R, ? extends E>(E ignored) -> none();
            };
            case None<T> ignored -> none();
        };
    }

    /**
     * Calls the provided {@code Consumer<T>} with the contained value (if {@code Some<T>}) and
     * returns itself. This allows for chaining multiple consumers that all need the value e.g.:
     * <pre>
     * {@code Option<String> stringOption = some("möp");}
     * {@code stringOption.inspect(System.out::println)}
     *             {@code .inspect(s -> System.out.printf("formated! %s", s));}
     * </pre>
     *
     * @param consumer function that accepts the value (if {@code Some<T>})
     *
     * @return the {@code Option<T>} it was called on
     */
    default @NotNull Option<T> inspect(@NotNull @NonNull  Consumer<? super @NotNull T> consumer) {
        if (this instanceof Some<T>(T value)) {
            consumer.accept(value);
        }
        return this;
    }

    /**
     * Returns the provided default result (if {@code None<T>}), or applies a {@code Fuction<T,R>}
     * to the contained value (if {@code Some}).
     * <p>
     * Arguments passed to {@link Option#mapOr(java.util.function.Function, Object)} are eagerly evaluated; if you are
     * passing the result of a function call, it is recommended to use
     * {@link Option#mapOrElse(java.util.function.Function, java.util.function.Supplier)}, which is lazily evaluated.
     *
     * @param defaultValue default value that is returned if {@code None<T>}
     * @param f            function that converts the value in case of {@code Some<T>}
     * @param <R>          the target type that should be mapped to
     *
     * @return the mapped value or the default value if {@code None<T>}
     */
    default <R> @NotNull R mapOr(
            @NotNull @NonNull Function<? super @NotNull T, ? extends @NotNull R> f, @NotNull R defaultValue) {
        return switch (this) {
            case Some<T>(T value) -> f.apply(value);
            case None<T> ignored -> defaultValue;
        };
    }

    /**
     * Computes a default function result (if {@code None}), or applies a different function to the
     * contained value (if {@code Some}).
     *
     * @param defaultSupplier {@code Supplier<? extends R>} that supplies a value if {@code None}
     * @param f               function that converts the value in case of {@code Some<T>}
     * @param <R>             the target type that should be mapped to
     *
     * @return the mapped value or the default value if {@code None}
     */
    default <R> @NotNull R mapOrElse(@NotNull @NonNull Function<? super @NotNull T, ? extends @NotNull R> f,
                                      @NotNull @NonNull Supplier<? extends @NotNull R> defaultSupplier) {
        return switch (this) {
            case Some<T>(T value) -> f.apply(value);
            case None<T> ignored -> defaultSupplier.get();
        };
    }

    /**
     * Transforms the {@code Option<T>} into a {@code Result<T, E>}, mapping {@code Some(v)} to
     * {@code Ok(v)} and {@code None} to {@code Err(err)}.
     * <p>
     * Arguments passed to {@link Option#okOr(Object)} are eagerly evaluated; if you are passing the
     * result of a function call, it is recommended to use {@link Option#okOrElse(java.util.function.Supplier)}, which
     * is lazily evaluated.
     *
     * @param err error value that should be returned.
     * @param <E> type of the error that should be returned
     *
     * @return a result representing the {@code Option<T>} in form of a {@code Result<T, E>}
     */
    @Contract(value = "_ -> new", pure = true)
    default <E> @NotNull Result<T, E> okOr(@NotNull @NonNull E err) {
        return switch (this) {
            case Some<T>(T value) -> Result.ok(value);
            case None<T> ignored -> Result.err(err);
        };
    }

    /**
     * Transforms the {@code Option<T>} into a {@code Result<T, E>}, mapping {@code Some(v)} to
     * {@code Ok(v)} and {@code None} to {@code Err(err())}.
     *
     * @param err a {@code Supplier<E>} of the error value that should be returned
     * @param <E> type of the error that should be returned
     *
     * @return a result representing the {@code Option<T>} in form of a {@code Result<T, E>}
     */
    @Contract("_ -> new")
    default <E> @NotNull Result<T, E> okOrElse(@NotNull @NonNull Supplier<? extends @NotNull E> err) {
        return switch (this) {
            case Some<T>(T value) -> Result.ok(value);
            case None<T> ignored -> Result.err(err.get());
        };
    }


    /**
     * Transforms the {@code Option<T>} into a {@code Condition<T>}, mapping {@code Some(v)} to {@code Holds(v, p)} and {@code None} to
     * {@code HoldsNot}.
     *
     * @param condition condition that should hold on the contained value
     * @return a {@code Condition<T>} representing the {@code Option<T>} in form of a {@code Condition<T>}
     */
    default @NotNull Condition<T> alwaysMaintain(@NotNull @NonNull Predicate<? super @NotNull T> condition){
        return switch (this){
            case Some<T>(T value) -> Condition.from(value, condition);
            case Option.None<T> ignored -> Condition.holdsNot();
        };
    }

    // TODO maybe make additional method with a more rust like iterator

    /**
     * Returns an {@code Iterator<T>} over the possibly contained value.
     *
     * @return an {@code Iterator<T>} over the possibly contained value
     */
    @Contract(pure = true)
    default @NotNull Iterator<T> iter() {
        return switch (this) {
            case Some<T>(T value) -> Stream.of(value)
                                       .iterator();
            case None<T> ignored -> Collections.emptyIterator();
        };
    }

    @Override
    @Contract(pure = true)
    default @NotNull Iterator<T> iterator() {
        return switch (this) {
            case Some<T>(T value) -> Stream.of(value)
                                       .iterator();
            case None<T> ignored -> Collections.emptyIterator();
        };
    }

    /**
     * Returns {@code None} if the {@code Option<T>} is {@code None}, otherwise returns
     * {@code other}.
     * <p>
     * Arguments passed to and are eagerly evaluated; if you are passing the result of a function
     * call, it is recommended to use {@link Option#andThen(java.util.function.Function)}, which is lazily evaluated.
     *
     * @param other other {@code Option<U>} that should be returned when this is {@code Some<T>}
     * @param <U>   type of the other optional
     *
     * @return either the {@code other} {@code Option<U>} or {@code None} when this is {@code None}
     */
    @Contract(pure = true)
    default <U> @NotNull Option<U> and(@NotNull @NonNull Option<U> other) {
        return switch (this) {
            case Some<T> ignored -> other;
            case None<T> ignored -> none();
        };
    }

    /**
     * Returns {@code None} if the {@code Option<T>} is {@code None}, otherwise calls the
     * {@code Function<T,Option<U>>} with the wrapped value and returns the result.
     * <p>
     * Some languages call this operation flatmap.
     *
     * @param other another optional that should be returned when this is {@code Some<T>}
     * @param <U>   type of the other optional
     *
     * @return either the result of {@code other(some.value)} or {@code None} when this is
     * {@code None}
     */
    default <U> @NotNull Option<? extends U> andThen(@NotNull @NonNull Function<? super @NotNull T, Option<? extends @NotNull U>> other) {
        return switch (this) {
            case Some<T>(T value) -> other.apply(value);
            case None<T> ignored -> none();
        };
    }

    /**
     * Returns {@code None} if the {@code Option<T>} is {@code None}, otherwise calls
     * {@code Predicate<T>} with the wrapped value and returns:
     * <ul>
     * <li>{@code Some(t)} if predicate returns {@code true} (where {@code t} is the wrapped
     * value), and
     * </li>
     * <li>{@code None} if predicate returns {@code false}.</li>
     * </ul>
     * <p>
     * This function works similar to Stream::filter(). You can imagine the Option<T> being an
     * iterator over one or zero elements. filter() lets
     * you decide which elements to keep.
     *
     * @param predicate predicate to filter the value on if it is {@code Some<T>}
     *
     * @return an option that is either {@code Some<T>} and conforms to the
     * {@code Predicate<? super T>} or {@code None<T>}
     */
    @Override
    @Contract("_ -> !null")
    default @NotNull Option<T> filter(@NotNull @NonNull Predicate<? super @NotNull T> predicate) {
        if (this instanceof Some<T>(T value)) {
            if (predicate.test(value)) {
                return some(value);
            }
        }
        return none();
    }

    /**
     * Returns the {@code Option<T>} if it contains a value, otherwise returns {@code other}.
     * <p>
     * Arguments passed to or are eagerly evaluated; if you are passing the result of a function
     * call, it is recommended to use {@link Option#orElse(java.util.function.Supplier)}, which is lazily evaluated.
     *
     * @param other the other {@code Option<T>} that should be returned instead if {@code this} is
     *              {@code None<T>}
     *
     * @return an {@code Option<T>} that is either {@code this} or the {@code other}
     * {@code Option<T>}
     */
    @Contract(pure = true)
    default @NotNull Option<T> or(@NotNull @NonNull Option<T> other) {
        return switch (this) {
            case Some<T>(T value) -> some(value);
            case None<T> ignored -> other;
        };
    }

    /**
     * Returns the {@code Option<T>} if it contains a value, otherwise returns {@code other} as an
     * {@code Option<T>}. This means if other is {@code null} the returned option is
     * {@code None<T>}.
     * <p>
     * Arguments passed to or are eagerly evaluated; if you are passing the result of a function
     * call, it is recommended to use {@link Option#orNullableElse(java.util.function.Supplier)}, which is lazily
     * evaluated.
     *
     * @param other the other {@code Option<T>} that should be returned instead if {@code this} is
     *              {@code None<T>}
     *
     * @return an {@code Option<T>} that is either {@code this} or the {@code other} wrapped in an
     * {@code Option<T>}
     */
    @Contract(value = "_ -> new", pure = true)
    default @NotNull Option<T> orNullable(@NotNull T other) {
        return switch (this) {
            case Some<T>(T value) -> some(value);
            case None<T> ignored -> Option.from(other);
        };
    }


    /**
     * Returns the option if it contains a value, otherwise calls f and returns the result.
     *
     * @param other a {@code Supplier} that provides the {@code other} {@code Option<T>} in case
     *              that {@code this} is {@code None<T>}
     *
     * @return the {@code Option<T>} if it contains a value, otherwise calls the {@code Supplier}
     * and returns the result
     */
    default <S extends T> @NotNull Option<? extends T> orElse(@NotNull @NonNull Supplier<@NotNull Option<S>> other) {
        return switch (this) {
            case Some<T>(T value) -> some(value);
            case None<T> ignored -> other.get();
        };
    }

    /**
     * Returns the option if it contains a value, otherwise calls f and returns the result wrapped
     * into an option.
     *
     * @param other a {@code Supplier} that provides the {@code other} {@code Option<T>} in case
     *              that {@code this} is {@code None<T>}
     *
     * @return the {@code Option<T>} if it contains a value, otherwise calls the {@code Supplier}
     * and returns the result in an {@code Option<T>}
     */
    @Contract("_ -> new")
    default <S extends T> @NotNull Option<T> orNullableElse(@NotNull @NonNull Supplier<S> other) {
        return switch (this) {
            case Some<T>(T value) -> some(value);
            case None<T> ignored -> Option.from(other.get());
        };
    }

    /**
     * Returns {@code Some<T>} if exactly one of {@code this}, {@code other} is {@code Some<T>},
     * otherwise returns {@code None<T>}.
     *
     * @param other the other {@code Option<T>}
     *
     * @return an {Option<T>} according to the above condition
     */
    @Contract(value = "_ -> new", pure = true)
    default @NotNull Option<T> xOr(@NotNull @NonNull Option<T> other) {
        return switch (this){
            case Some<T>(T thisValue) when !(other instanceof Some<T>) -> some(thisValue);
            case None<T> ignored when other instanceof Some<T>(T otherValue) -> some(otherValue);
            default -> none();
        };
    }

    /**
     * Creates a Java {@link java.util.Optional} out of the option.
     *
     * @return an optional containing the value of the option
     */
    @Contract(value = "-> new", pure = true)
    @Override
    default @NotNull Optional<T> j() {
        return toOptional();
    }

    /**
     * Creates a Java {@link java.util.Optional} out of the option.
     *
     * @return an optional containing the value of the option
     */
    @Contract(value = "-> new", pure = true)
    default @NotNull Optional<T> toOptional() {
        return switch (this) {
            case Some<T>(T value) -> Optional.of(value);
            case None<T> ignored -> Optional.empty();
        };
    }

    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull Option<T> optionFrom(@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
                                             @NotNull @NonNull Optional<T> optional) {
        return optional.map(Option::some)
                       .orElseGet(Option::none);
    }

    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull Option<T> from(T value) {
        if (value == null) {
            return none();
        }
        return some(value);
    }

    /**
     * Allows for this to be returned by Spring JPA repositories.
     */
    static <T> @NotNull Option<T> of(@NotNull @NonNull Streamable<T> streamable) {
        return switch (MultiOption.of(streamable)) {
            case MultiOption.Many<T> ignored -> none();
            case MultiOption.One<T>(T value) -> some(value);
            case MultiOption.None<T> ignored -> none();
        };
    }


    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull Option<T> some(@NotNull T value) {
        return new Some<>(value);
    }

    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull Result<Option<T>, GivenValueWasNullError> someSafe(@NotNull T value){
        //noinspection ConstantValue
        if (value != null) {
            return Result.ok(some(value));
        } else {
            return Result.err(new GivenValueWasNullError("The value given was null despite the contract forbidding that!"));
        }
    }

    @Contract(value = "-> !null", pure = true)
    @SuppressWarnings("unchecked")
    static <T> @NotNull Option<T> none() {
        return (Option<T>) NONE;
    }

    @Override
    @Contract(value = "-> new", pure = true)
    default @NotNull ControlFlow<Option<Infallible>, T> branch() {
        return switch (this) {
            case Some<T>(T value) -> new ControlFlow.Continue<>(value);
            case None<T> ignored -> new ControlFlow.Break<>(none());
        };
    }
}
