package the.oronco.adt;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import the.oronco.Rusty;
import the.oronco.adt.exceptions.ValueNotValidForBoundError;

/**
 * Condition describes a Monad that always asserts a certain condition over the contained value.
 *
 * @param <T>
 **/
public sealed interface Condition<T> extends Rusty<Optional<T>> {
    HoldsNot<?> HOLDS_NOT = new HoldsNot<>();
    Predicate<Object> ALWAYS_TRUE = ignored -> true;

    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class HoldsNot<T> implements Condition<@NotNull T> {
        @SuppressWarnings("unchecked")
        public <U> Condition<U> as(){
            return (Condition<U>) HOLDS_NOT;
        }
    }

    record Holds<T>(@NotNull @NonNull T value, @NotNull @NonNull Predicate<? super T> bound) implements Condition<@NotNull T> {
        public Holds {
            if (bound.test(value)) {
                throw new ValueNotValidForBoundError("The value %s is not valid for the given bound!".formatted(value));
            }
        }
    }

    default boolean doesHold() {
        return switch (this) {
            case Holds<T> ignored -> true;
            case HoldsNot<T> ignored -> false;
        };
    }

    default boolean doesNotHold() {
        return switch (this) {
            case Holds<T> ignored -> false;
            case HoldsNot<T> ignored -> true;
        };
    }

    /**
     * Returns true if the option is a Holds and the value inside it matches a predicate.
     *
     * @param predicate predicate to evaluate the value against if the {@code Condition<T>} is {@code Holds<T>}
     * @return if the value exists && the predicate matches
     */
    default boolean isHoldsAnd(@NotNull @NonNull Predicate<? super @NotNull T> predicate) {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> predicate.test(value);
            case HoldsNot<T> ignored -> false;
        };
    }

    /**
     * Creates a {@link java.util.stream.Stream} from the {@code Condition<T>} that contains the value of {@code Holds<T>} and is empty otherwise.
     *
     * @return the stream that contains the value of {@code Holds<T>} and is empty otherwise
     */
    default @NotNull Stream<T> stream() {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> Stream.of(value);
            case HoldsNot<T> ignored -> Stream.of();
        };
    }

    /**
     * @param errorMessage the error message that is thrown when the {@code Condition<T>} is {@code HoldsNot<T>}
     * @return the value if the {@code Condition<T>} is {@code Holds<T>}
     * @throws java.util.NoSuchElementException with the given Error message when the {@code Condition<T>} is {@code HoldsNot<T>}
     */
    default @NotNull T expect(String errorMessage) throws @NotNull NoSuchElementException {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> value;
            case HoldsNot<T> ignored -> throw new NoSuchElementException(errorMessage);
        };
    }

    /**
     * @param errorMessage the Exception that is thrown when the {@code Condition<T>} is {@code HoldsNot<T>}
     * @return the value if the {@code Condition<T>} is {@code Holds<T>}
     * @throws E with the given Exception when the {@code Condition<T>} is {@code HoldsNot<T>}
     */
    default @NotNull <E extends Exception> T expect(@NonNull E errorMessage) throws @NotNull E {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> value;
            case HoldsNot<T> ignored -> throw errorMessage;
        };
    }

    /**
     * @param exceptionSupplier supplier that returns the Exception that is thrown when the {@code Condition<T>} is {@code HoldsNot<T>}
     * @return the value if the {@code Condition<T>} is {@code Holds<T>}
     * @throws E with the given Exception when the {@code Condition<T>} is {@code HoldsNot<T>}
     */
    default @NotNull <E extends Exception> T expectElse(@NotNull @NonNull Supplier<@NotNull E> exceptionSupplier) throws @NotNull E {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> value;
            case HoldsNot<T> ignored -> throw exceptionSupplier.get();
        };
    }

    /**
     * Returns the contained {@code Holds<T>} value and throws otherwise. (Similar to {@link Condition#expect(String)} but without a custom error
     * message)
     * <p>
     * The usage of this method is discouraged as control flow through exceptions can be hard to understand and organize. Use
     * {@link Condition#unwrapOr(Object)} or {@link Condition#unwrapOrElse(java.util.function.Supplier)} instead.
     *
     * @return the value if the {@code Condition<T>} is {@code Holds<T>}
     * @throws java.util.NoSuchElementException when the {@code Condition<T>} is {@code HoldsNot<T>}
     */
    default @NotNull T unwrap() throws @NotNull NoSuchElementException {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> value;
            case HoldsNot<T> ignored -> throw new NoSuchElementException("Condition was unwrapped but it had no value!");
        };
    }

    /**
     * Returns the contained {@code Holds<T>} value and a default value otherwise.<p> Arguments passed to {@link Condition#unwrapOr(T)} are eagerly
     * evaluated; if you are passing the result of a function call, it is recommended to use
     * {@link Condition#unwrapOrElse(java.util.function.Supplier)}, which is lazily evaluated.
     *
     * @param defaultValue default value that should be returned in the case that {@code Condition<T>} is {@code HoldsNot<T>}
     * @return the value if the {@code Condition<T>} is {@code Holds<T>} and the default value otherwise
     */
    default T unwrapOr(T defaultValue) {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> value;
            case HoldsNot<T> ignored -> defaultValue;
        };
    }

    /**
     * Returns the contained {@code Holds<T>} value or computes it with the given {@code Supplier<T>}.
     *
     * @param supplier the supplier that computes the value if {@code Condition<T>} is {@code HoldsNot<T>}
     * @return the value in {@code Holds<T>} or the result of the {@code Supplier<T>}
     */
    default T unwrapOrElse(@NotNull @NonNull Supplier<? extends @NotNull T> supplier) {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> value;
            case HoldsNot<T> ignored -> supplier.get();
        };
    }

    /**
     * Maps an {@code Condition<T>} to {@code Condition<T>} by applying a function to a contained value (if Holds and the condition holds for the new
     * value) or returns HoldsNot (if HoldsNot) otherwise.
     *
     * @param f function that converts from one {@code T} to another {@code T}
     * @return a new {@code Condition<R>} with the converted value
     */
    default @NotNull Condition<T> map(@NotNull @NonNull Function<? super @NotNull T, ? extends @NotNull T> f) {
        return switch (this) {
            case Holds<T>(T value, var bound) -> from(f.apply(value), bound);
            case HoldsNot<T> ignored -> holdsNot();
        };
    }

    /**
     * Maps an {@code Condition<T>} to {@code Condition<R>} by applying a function to a contained value (if Holds and the new bound holds for the new
     * value) or returns HoldsNot (if HoldsNot) otherwise.
     *
     * @param <R>      the new type of the contained value
     * @param f        function that converts {@code T} to {@code R}
     * @param newBound the new bound that governs the contained value
     * @return a new {@code Condition<R>} with the converted value
     */
    default @NotNull <R> Condition<R> map(
            @NotNull @NonNull Function<? super @NotNull T, ? extends @NotNull R> f, @NotNull @NonNull Predicate<? super @NotNull R> newBound) {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> from(f.apply(value), newBound);
            case HoldsNot<T> ignored -> holdsNot();
        };
    }

    /**
     * Creates a new {@code Condition<T>} with the same value and the new bound. This is a {@code Holds<T>} if the new bound holds for the current
     * value, otherwise returns a {@code HoldsNot<T>}.
     *
     * @param bound the new bound that governs the contained value
     * @return a new {@code Condition<R>} with a new bound
     */
    default @NotNull Condition<T> reBound(
            @NotNull @NonNull Predicate<? super @NotNull T> bound) {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> from(value, bound);
            case HoldsNot<T> ignored -> holdsNot();
        };
    }

    /**
     * Returns {@code None} if the {@code Option<T>} is {@code None}, otherwise calls {@code Predicate<T>} with the wrapped value and returns:
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
     * @return an option that is either {@code Some<T>} and conforms to the {@code Predicate<? super T>} or {@code None<T>}
     */
    default @NotNull Condition<T> filter(@NotNull @NonNull Predicate<? super @NotNull T> predicate) {
        if (this instanceof Holds<T>(T value, var bound)) {
            if (predicate.test(value)) {
                return holds(value, bound);
            }
        }
        return holdsNot();
    }

    /**
     * Creates a Java {@link java.util.Optional} out of the option.
     *
     * @return an optional containing the value of the option
     */
    @Override
    default @NotNull Optional<T> j() {
        return toOptional();
    }

    /**
     * Creates a Java {@link java.util.Optional} out of the option.
     *
     * @return an optional containing the value of the option
     */
    default @NotNull Optional<T> toOptional() {
        return switch (this) {
            case Holds<T>(T value, var ignored) -> Optional.of(value);
            case HoldsNot<T> ignored -> Optional.empty();
        };
    }

    static <T> @NotNull Condition<T> conditionFrom(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") @NotNull @NonNull Optional<T> optional,
            @NotNull @NonNull Predicate<? super @NotNull T> bound) {
        return optional.map(val -> from(val, bound))
                       .orElseGet(Condition::holdsNot);
    }

    static <T> @NotNull Condition<T> from(T value, @NotNull Predicate<? super @NotNull T> bound) {
        return Option.from(value)
                     .map(v -> holds(v, bound))
                     .unwrapOr(holdsNot());
    }
    static <T> @NotNull Condition<T> from(T value) {
        return Option.from(value)
                     .map(Condition::holds)
                     .unwrapOrElse(Condition::holdsNot);

    }

    static <T> @NotNull Condition<T> holds(@NotNull T value, @NotNull Predicate<? super @NotNull T> bound) throws ValueNotValidForBoundError {
        if (bound.test(value)) {
            return new Holds<>(value, bound);
        }
        throw createValueNotInBoundError(value);
    }

    /**
     * Creates a {@code Holds<T>} with a condition that always returns true. This is basically an {@link the.oronco.adt.Option} with worse performance
     * as the condition is regularly checked.
     *
     * @param value the value that should be wrapped
     * @param <T>   the type of the value
     * @return a Condition that always holds true and is thus always a {@code Holds<T>}
     */
    static <T> @NotNull Condition<T> holds(@NotNull T value) {
        return holds(value, ALWAYS_TRUE);
    }

    static <T> @NotNull Result<Condition<T>, ValueNotValidForBoundError> holdsSafe(@NotNull T value, @NotNull Predicate<? super @NotNull T> bound) {
        //noinspection ConstantValue
        if (value != null && bound.test(value)) {
            return Result.ok(new Holds<>(value, bound));
        } else {
            return Result.err(createValueNotInBoundError(value));
        }
    }

    @NotNull
    private static <T> ValueNotValidForBoundError createValueNotInBoundError(@NotNull T value) {
        return new ValueNotValidForBoundError("Cannot create a condition with a value (%s) and a bound that does not allow the given value!".formatted(
                value));
    }


    @SuppressWarnings("unchecked")
    static <T> @NotNull Condition<T> holdsNot() {
        return (Condition<T>) HOLDS_NOT;
    }

}
