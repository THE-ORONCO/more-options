package the.oronco.adt;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.util.Streamable;
import the.oronco.Rusty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO examples like in the rust documentation
// TODO replace exceptions with better exceptions
// TODO change naming scheme to be more java like (e.g. .unwrapOr -> .orElse, .unwrapOrElse ->
//  .orElseGet)
// TODO tests

/**
 * This algebraic type describes a value that can be either none, one or values values.
 *
 * @param <T> Type of the value (needed for type save code)
 */
@Unmodifiable
public sealed interface MultiOption<T> extends Rusty<Collection<T>>, Streamable<T>, Serializable {
    None<?> NONE = new None<>();

    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Unmodifiable
    final class None<T> implements MultiOption<@NotNull T> {}
    @Unmodifiable
    record One<T>(@NotNull @NonNull T value) implements MultiOption<@NotNull T> {
    }
    @Unmodifiable record Many<T>(@NotNull @NonNull Collection<@NotNull @NonNull T> values) implements MultiOption<@NotNull T> {
        public Many {
            if (!values.stream()
                       .allMatch(Objects::nonNull)) {
                throw new NullPointerException("No null values are allowed in a MultiOption!");
            }
        }
    }


    @Contract(pure = true)
    default boolean isNone() {
        return switch (this) {
            case None<T> ignored -> true;
            case One<T> ignored -> false;
            case Many<T> ignored -> false;
        };
    }

    @Contract(pure = true)
    default boolean isOne() {
        return switch (this) {
            case None<T> ignored -> false;
            case One<T> ignored -> true;
            case Many<T> ignored -> false;
        };
    }

    /**
     * Returns true if the {@code MultiOption<T>} is a {@code One<T>} and the value inside it
     * matches a predicate.
     *
     * @param predicate predicate to evaluate the value against if the {@code MultiOption<T>} is
     *                  {@code One<T>}
     *
     * @return if the value exists && the predicate matches
     */
    default boolean isOneAnd(@NotNull @NonNull Predicate<? super @NotNull T> predicate) {
        return switch (this) {
            case None<T> ignored -> false;
            case One<T>(T value) -> predicate.test(value);
            case Many<T> ignored -> false;
        };
    }

    @Contract(pure = true)
    default boolean isMany() {
        return switch (this) {
            case None<T> ignored -> false;
            case One<T> ignored -> false;
            case Many<T> ignored -> true;
        };
    }

    /**
     * Returns true if the {@code MultiOption<T>} is a {@code Many<T>} and the value inside it
     * matches a predicate.
     *
     * @param predicate predicate to evaluate the value against if the {@code MultiOption<T>} is
     *                  {@code Many<T>}
     *
     * @return if the value exists && the predicate matches
     */
    default boolean isManyAnd(@NotNull @NonNull Predicate<? super @NotNull Collection<? super @NotNull T>> predicate) {
        return switch (this) {
            case None<T> ignored -> false;
            case One<T> ignored -> false;
            case Many<T>(var values) -> predicate.test(values);
        };
    }

    /**
     * Creates a {@link Stream} from the {@code MultiOption<T>} that contains the values of
     * {@code One<T>} or {@code Many<T>} and is empty otherwise.
     *
     * @return the stream that contains the value of {@code Some<T>}  or {@code Many<T>} and is
     * empty otherwise
     */
    @Override
    @Contract(value = "-> new", pure = true)
    default @NotNull Stream<T> stream() {
        return switch (this) {
            case None<T> ignored -> Stream.of();
            case One<T>(T value) -> Stream.of(value);
            case Many<T>(var values) -> values.stream();
        };
    }

    @Override
    @Contract(value = "-> new", pure = true)
    default @NotNull Set<T> toSet() {
        return switch (this) {
            case None<T> ignored -> Set.of();
            case One<T>(T value) -> Set.of(value);
            case Many<T>(var values) -> Set.copyOf(values);
        };
    }

    @Override
    @Contract(value = "-> new", pure = true)
    default @NotNull List<T> toList() {
        return switch (this) {
            case None<T> ignored -> List.of();
            case One<T>(T value) -> List.of(value);
            case Many<T>(var values) -> List.copyOf(values);
        };
    }

    @Override
    @Contract(value = "-> new", pure = true)
    default @NotNull Iterator<T> iterator() {
        return switch (this) {
            case None<T> ignored -> Collections.emptyIterator();
            case One<T>(T value) -> Stream.of(value)
                                     .iterator();
            case Many<T>(var values) -> values.iterator();
        };
    }

    default <C> @NotNull C wrap(@NotNull @NonNull Function<? super @NotNull Collection<? extends @NotNull T>, ? extends @NotNull C> wrap) {
        return switch (this) {
            case None<T> ignored -> wrap.apply(Collections.emptyList());
            case One<T>(T value) -> wrap.apply(Collections.singleton(value));
            case Many<T>(var values) -> wrap.apply(values);
        };
    }

    /**
     * @param errorMessage the error message that is thrown when the {@code Option<T>} is
     *                     {@code None<T>}
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>}
     *
     * @throws NoSuchElementException with the given Error message when the {@code Option<T>} is
     *                                {@code None<T>}
     */
    default @NotNull Collection<T> unwrap(@NotNull @NonNull String errorMessage) throws
                                                                                 @NotNull NoSuchElementException {
        return switch (this) {
            case None<T> ignored -> throw new NoSuchElementException(errorMessage);
            case One<T>(T value) -> new ArrayList<>(Collections.singletonList(value));
            case Many<T>(var values) -> new ArrayList<>(values);
        };
    }

    /**
     * @param exception the exception that is thrown when the {@code Option<T>} is {@code None<T>}
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>}
     *
     * @throws NoSuchElementException with the given Error message when the {@code Option<T>} is
     *                                {@code None<T>}
     */
    default <X extends Exception> @NotNull Collection<T> unwrap(@NotNull @NonNull X exception) throws
                                                                                               @NotNull X {
        return switch (this) {
            case None<T> ignored -> throw exception;
            case One<T>(T value) -> new ArrayList<>(Collections.singletonList(value));
            case Many<T>(var values) -> new ArrayList<>(values);
        };
    }

    /**
     * @param exceptionSupplier the supplier that supplies the exception that is thrown when the
     *                          {@code Option<T>} is {@code None<T>}
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>}
     *
     * @throws NoSuchElementException with the given Error message when the {@code Option<T>} is
     *                                {@code None<T>}
     */
    default <X extends Exception> @NotNull Collection<T> unwrapElse(@NotNull @NonNull Supplier<? extends @NotNull X> exceptionSupplier) throws
                                                                                                                     @NotNull X {

        return switch (this) {
            case None<T> ignored -> throw exceptionSupplier.get();
            case One<T>(T value) -> new ArrayList<>(Collections.singletonList(value));
            case Many<T>(var values) -> new ArrayList<>(values);
        };
    }

    /**
     * Returns the contained {@code One<T>} value or {@code Many<T>} values and throws otherwise.
     * (Similar to {@link MultiOption#unwrap(String)} but, without a custom error message)
     * <p>
     * The usage of this method is discouraged as control flow through exceptions can be hard to
     * understand and organize. Use {@link MultiOption#unwrapOr(Collection)} or
     * {@link MultiOption#unwrapOrElse(Supplier)} instead.
     *
     * @return the value(s) if the {@code MultiOption<T>} is {@code One<T>} or {@code Many<T>}
     *
     * @throws NoSuchElementException when the {@code MultiOption<T>} is {@code None<T>}
     */
    default @NotNull Collection<T> unwrap() throws @NotNull NoSuchElementException {
        return switch (this) {
            case None<T> ignored ->
                    throw new NoSuchElementException("Option was unwrapped but it had no value!");
            case One<T>(T value) -> new ArrayList<>(Collections.singleton(value));
            case Many<T>(var values) -> new ArrayList<>(values);
        };
    }

    /**
     * Returns the contained {@code One<T>} value or {@code Many<T>} values and a default value
     * otherwise.<p> Arguments passed to {@link MultiOption#unwrapOr(Collection)} are eagerly
     * evaluated; if you are passing the result of a function call, it is recommended to use
     * {@link MultiOption#unwrapOrElse(Supplier)}, which is lazily evaluated.
     *
     * @param defaultValue default value that should be returned in the case that {@code Option<T>}
     *                     is {@code None<T>}
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>} and the default value otherwise
     */
    default @NotNull Collection<T> unwrapOr(@NotNull @NonNull Collection<@NotNull T> defaultValue) {
        return switch (this) {
            case None<T> ignored -> defaultValue;
            case One<T>(T value) -> new ArrayList<>(Collections.singleton(value));
            case Many<T>(var values) -> new ArrayList<>(values);
        };
    }

    /**
     * Returns the contained {@code One<T>} value or {@code Many<T>} values or computes it with the
     * given {@code Supplier<T>}.
     *
     * @param supplier the supplier that computes the value if {@code MultiOption<T>} is
     *                 {@code None<T>} or {@code Many<T>}
     *
     * @return the value in {@code Some<T>} or {@code Many<T>} or the result of the
     * {@code Supplier<T>}
     */
    default @NotNull Collection<T> unwrapOrElse(@NotNull @NonNull Supplier<? extends @NotNull Collection<@NotNull T>> supplier) {
        return switch (this) {
            case None<T> ignored -> supplier.get();
            case One<T>(T value) -> new ArrayList<>(Collections.singleton(value));
            case Many<T>(var values) -> new ArrayList<>(values);
        };
    }

    /**
     * Maps an {@code MultiOption<T>} to {@code MultiOption<R>} by applying a function to a
     * contained value (if {@code One<T>}) or values ({@code Many<T>} returns None (if
     * {@code None}).
     *
     * @param f   function that converts {@code T} to {@code R}
     * @param <R> type that the value of a {@code Some<T>} should be converted to
     *
     * @return a new {@code MultiOption<R>} with the converted value(s)
     */
    @Override
    default <R> @NotNull MultiOption<R> map(@NotNull @NonNull Function<? super @NotNull T, ? extends @NotNull R> f) {
        return switch (this) {
            case None<T> ignored -> none();
            case One<T>(T value) -> one(f.apply(value));
            case Many<T>(var values) -> many(values.stream()
                                                 .map(f)
                                                 .collect(Collectors.toList()));
        };
    }

    /**
     * Calls the provided {@code Consumer<T>} with the contained value(s) (if {@code One<T>} or
     * {@code Many<T>}) and returns itself. This allows for chaining multiple consumers that all
     * need the value(s) e.g.:
     * <pre>
     * {@code MultiOption<String> stringOption = one("möp");}
     * {@code stringOption.inspect(System.out::println)}
     *             {@code .inspect(s -> System.out.printf("formated! %s", s));}
     * </pre>
     *
     * @param consumer function that accepts the value(s) (if {@code One<T>} or {@code Many<T>})
     *
     * @return the {@code MultiOption<T>} it was called on
     */
    default @NotNull MultiOption<T> inspect(@NotNull @NonNull Consumer<? super @NotNull Collection<? super @NotNull T>> consumer) {
        switch (this) {
            case None<T> ignored -> {
            }
            case One<T>(T value) -> consumer.accept(Collections.singletonList(value));
            case Many<T>(var values) -> consumer.accept(values);
        }

        return this;
    }

    /**
     * Returns the provided default result (if {@code None<T>}), or applies a {@code Fuction<T,R>}
     * to the contained value (if {@code One}) or values (if {@code Many<T>}).
     * <p>
     * Arguments passed to {@link MultiOption#mapOr(Function, Collection)} are eagerly evaluated; if
     * you are passing the result of a function call, it is recommended to use
     * {@link MultiOption#mapOrElse(Function, Supplier)}, which is lazily evaluated.
     *
     * @param defaultValue default value that is returned if {@code None<T>}
     * @param f            function that converts the value in case of {@code One<T>} and values in
     *                     case of {@code Many<T>}
     * @param <R>          the target type that should be mapped to
     *
     * @return the mapped value or the default value if {@code None<T>}
     */
    default <R> @NotNull Collection<R> mapOr(@NotNull @NonNull Function<? super @NotNull T, ? extends @NotNull R> f,
                                             @NotNull @NonNull Collection<@NotNull R> defaultValue) {
        return switch (this) {
            case None<T> ignored -> defaultValue;
            case One<T>(T value) -> Collections.singletonList(f.apply(value));
            case Many<T>(var values) -> values.stream()
                                            .map(f)
                                            .collect(Collectors.toList());
        };
    }

    /**
     * + Computes a default function result (if {@code None}), or applies a different function to
     * the contained value (if {@code One}) or values (if {@code Many<T>}).
     *
     * @param defaultSupplier {@code Supplier} that supplies a
     *                        value(s) if {@code None}
     * @param f               function that converts the value in case of {@code Some<T>} and values
     *                        of {@code Many<T>}
     * @param <R>             the target type that should be mapped to
     *
     * @return the mapped value or the default value if {@code None}
     */
    default <R> @NotNull Collection<R> mapOrElse(@NotNull @NonNull Function<? super @NotNull T, ? extends @NotNull R> f,
                                                 @NotNull @NonNull Supplier<? extends @NotNull Collection<@NotNull R>> defaultSupplier) {
        return switch (this) {
            case None<T> ignored -> defaultSupplier.get();
            case One<T>(T value) -> Collections.singletonList(f.apply(value));
            case Many<T>(var values) -> values.stream()
                                            .map(f)
                                            .collect(Collectors.toList());
        };
    }


    /**
     * Creates a MultiOption from an iterator of things according to the following rules:
     * <table>
     *   <tr>
     *     <td>empty iterator</td>
     *     <td>{@link None}</td>
     *   </tr>
     *   <tr>
     *     <td>iterator with one element</td>
     *    <td>{@link One}</td>
     *   </tr>
     *   <tr>
     *     <td>iterator with multiple elements</td>
     *     <td>{@link Many}</td>
     *   </tr>
     * </table>
     *
     * @param unknownAmount an iterator of unknown size that should be turned into a
     *                      {@link MultiOption}
     * @param <T>           type of the list elements
     *
     * @return a {@link MultiOption} according to the above rules
     */
    static <T> @NotNull MultiOption<T> from(@NotNull @NonNull Iterator<? extends @NotNull T> unknownAmount) {
        ArrayList<T> accumulator = new ArrayList<>();
        while (unknownAmount.hasNext()) {
            accumulator.add(unknownAmount.next());
        }
        return from(accumulator);
    }

    /**
     * Creates a MultiOption from an iterable of things according to the following rules:
     * <table>
     *   <tr>
     *     <td>empty iterable</td>
     *     <td>{@link None}</td>
     *   </tr>
     *   <tr>
     *     <td>iterable with one element</td>
     *    <td>{@link One}</td>
     *   </tr>
     *   <tr>
     *     <td>iterable with multiple elements</td>
     *     <td>{@link Many}</td>
     *   </tr>
     * </table>
     *
     * @param unknownAmount an iterable of unknown size that should be turned into a
     *                      {@link MultiOption}
     * @param <T>           type of the list elements
     *
     * @return a {@link MultiOption} according to the above rules
     */
    static <T> @NotNull MultiOption<T> from(@NotNull @NonNull Iterable<? extends @NotNull T> unknownAmount) {
        ArrayList<T> accumulator = new ArrayList<>();
        for (T t : unknownAmount) {
            accumulator.add(t);
        }
        return from(accumulator);
    }

    /**
     * Creates a MultiOption from a collection of things according to the following rules:
     * <table>
     *   <tr>
     *     <td>empty collection</td>
     *     <td>{@link None}</td>
     *   </tr>
     *   <tr>
     *     <td>collection with one element</td>
     *    <td>{@link One}</td>
     *   </tr>
     *   <tr>
     *     <td>collection with multiple elements</td>
     *     <td>{@link Many}</td>
     *   </tr>
     * </table>
     *
     * @param unknownAmount a collection of unknown size that should be turned into a
     *                      {@link MultiOption}
     * @param <T>           type of the list elements
     *
     * @return a {@link MultiOption} according to the above rules
     */
    static <T> @NotNull MultiOption<T> from(Collection<@NotNull T> unknownAmount) {
        if (unknownAmount == null || unknownAmount.isEmpty()) {
            return none();

        } else if (unknownAmount.size() == 1) {
            var it = unknownAmount.iterator();
            if (it.hasNext()) {
                return one(it.next());
            } else {
                return none();
            }

        } else {
            return many(unknownAmount);
        }
    }

    static <T> @NotNull MultiOption<T> fromMultiOptions(@NotNull @NonNull Collection<@NotNull MultiOption<@NotNull T>> multipleMultiOptions) {
        return MultiOption.from(multipleMultiOptions.stream()
                                                    .flatMap(multiOption -> switch (multiOption) {
                                                        case None<T> ignored -> Stream.of();
                                                        case One<T>(T value) -> Stream.of(value);
                                                        case Many<T>(var values) -> values.stream();
                                                    })
                                                    .collect(Collectors.toSet()));
    }

    /**
     * Allows for this to be returned by Spring JPA repositories.
     */
    static <T> @NotNull MultiOption<T> of(@NotNull @NonNull Streamable<T> streamable) {
        return MultiOption.from(streamable);
    }

    default int size() {
        return switch (this) {
            case None<T> ignored -> 0;
            case One<T> ignored -> 1;
            case Many<T>(var values) -> values.size();
        };
    }

    @Override
    default @NotNull Collection<T> j() {
        return switch (this) {
            case None<T> ignored -> Collections.emptyList();
            case One<T>(T value) -> Collections.singleton(value);
            case Many<T>(var values) -> values;
        };
    }

    @SuppressWarnings("unchecked")
    static <T> @NotNull MultiOption<T> none() {
        return (MultiOption<T>) NONE;
    }

    static <T> @NotNull MultiOption<T> one(T single) {
        return new One<>(single);
    }

    static <T> @NotNull MultiOption<T> many(@NotNull @NonNull Collection<@NotNull T> many) {
        return (MultiOption<T>) new Many<>(many);
    }

    @SafeVarargs
    static <T> @NotNull MultiOption<T> many(@NotNull @NonNull T... many) {
        return many(Arrays.asList(many));
    }
}
