package the.oronco.adt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO examples like in the rust documentation
// TODO replace exceptions with better exceptions
// TODO change naming scheme to be more java like (e.g. .unwrapOr -> .orElse, .unwrapOrElse -> .orElseGet)
// TODO tests

/**
 * This algebraic type describes a value that can be either none, one or values values.
 *
 * @param <T> Type of the value (needed for type save code)
 */
public sealed interface MultiOption<T> {
    None<?> NONE = new None<>();

    record None<T>() implements MultiOption<T> {}

    record One<T>(T value) implements MultiOption<T> {}

    record Many<T>(Collection<T> values) implements MultiOption<T> {
        public Set<T> manySet() {
            return new HashSet<>(values);
        }

        public List<T> manyList() {
            return new ArrayList<>(values);
        }
    }

    default boolean isNone() {
        return switch (this) {
            case None<T> ignored -> true;
            case One<T> ignored -> false;
            case Many<T> ignored -> false;
        };
    }

    default boolean isOne() {
        return switch (this) {
            case None<T> ignored -> false;
            case One<T> ignored -> true;
            case Many<T> ignored -> false;
        };
    }

    /**
     * Returns true if the {@code MultiOption<T>} is a {@code One<T>} and the value inside it matches a predicate.
     *
     * @param predicate predicate to evaluate the value against if the {@code MultiOption<T>} is {@code One<T>}
     *
     * @return if the value exists && the predicate matches
     */
    default boolean isOneAnd(Predicate<? super T> predicate) {
        return switch (this) {
            case None<T> ignored -> false;
            case One<T> one -> predicate.test(one.value);
            case Many<T> ignored -> false;
        };
    }

    default boolean isMany() {
        return switch (this) {
            case None<T> ignored -> false;
            case One<T> ignored -> false;
            case Many<T> ignored -> true;
        };
    }

    /**
     * Returns true if the {@code MultiOption<T>} is a {@code Many<T>} and the value inside it matches a predicate.
     *
     * @param predicate predicate to evaluate the value against if the {@code MultiOption<T>} is {@code Many<T>}
     *
     * @return if the value exists && the predicate matches
     */
    default boolean isManyAnd(Predicate<? super Collection<T>> predicate) {
        return switch (this) {
            case None<T> ignored -> false;
            case One<T> ignored -> false;
            case Many<T> many -> predicate.test(many.values);
        };
    }

    /**
     * Creates a {@link Stream} from the {@code MultiOption<T>} that contains the values of {@code One<T>} or {@code Many<T>} and is empty
     * otherwise.
     *
     * @return the stream that contains the value of {@code Some<T>}  or {@code Many<T>} and is empty otherwise
     */
    default Stream<T> stream() {
        return switch (this) {
            case None<T> ignored -> Stream.of();
            case One<T> one -> Stream.of(one.value);
            case Many<T> many -> many.values.stream();
        };
    }

    /**
     * @param errorMessage the error message that is thrown when the {@code Option<T>} is {@code None<T>}
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>}
     *
     * @throws NoSuchElementException with the given Error message when the {@code Option<T>} is {@code None<T>}
     */
    default Collection<T> expect(String errorMessage) throws NoSuchElementException {
        return switch (this) {
            case None<T> ignored -> throw new NoSuchElementException(errorMessage);
            case One<T> one -> new ArrayList<>(Collections.singletonList(one.value));
            case Many<T> many -> new ArrayList<>(many.values);
        };
    }

    /**
     * Returns the contained {@code One<T>} value or {@code Many<T>} values and throws otherwise. (Similar to
     * {@link MultiOption#expect(String)} but, without a custom error message)
     * <p>
     * The usage of this method is discouraged as control flow through exceptions can be hard to understand and organize. Use
     * {@link MultiOption#unwrapOr(Collection)} or {@link MultiOption#unwrapOrElse(Supplier)} instead.
     *
     * @return the value(s) if the {@code MultiOption<T>} is {@code One<T>} or {@code Many<T>}
     *
     * @throws NoSuchElementException when the {@code MultiOption<T>} is {@code None<T>}
     */
    default Collection<T> unwrap() throws NoSuchElementException {
        return switch (this) {
            case None<T> ignored -> throw new NoSuchElementException("Option was unwrapped but it had no value!");
            case One<T> one -> new ArrayList<>(Collections.singleton(one.value));
            case Many<T> many -> new ArrayList<>(many.values);
        };
    }

    /**
     * Returns the contained {@code One<T>} value or {@Many} values and a default value otherwise.<p> Arguments passed to
     * {@link MultiOption#unwrapOr(Collection)} are eagerly evaluated; if you are passing the result of a function call, it is recommended
     * to use {@link MultiOption#unwrapOrElse(Supplier)}, which is lazily evaluated.
     *
     * @param defaultValue default value that should be returned in the case that {@code Option<T>} is {@code None<T>}
     *
     * @return the value if the {@code Option<T>} is {@code Some<T>} and the default value otherwise
     */
    default Collection<T> unwrapOr(Collection<T> defaultValue) {
        return switch (this) {
            case None<T> ignored -> defaultValue;
            case One<T> one -> new ArrayList<>(Collections.singleton(one.value));
            case Many<T> many -> new ArrayList<>(many.values);
        };
    }

    /**
     * Returns the contained {@code One<T>} value or {@code Many<T>} values or computes it with the given {@code Supplier<T>}.
     *
     * @param supplier the supplier that computes the value if {@code MultiOption<T>} is {@code None<T>} or {@code Many<T>}
     *
     * @return the value in {@code Some<T>} or {@code Many<T>} or the result of the {@code Supplier<T>}
     */
    default Collection<T> unwrapOrElse(Supplier<Collection<T>> supplier) {
        return switch (this) {
            case None<T> ignored -> supplier.get();
            case One<T> one -> new ArrayList<>(Collections.singleton(one.value));
            case Many<T> many -> new ArrayList<>(many.values);
        };
    }

    /**
     * Maps an {@code MultiOption<T>} to {@code MultiOption<R>} by applying a function to a contained value (if {@code One<T>}) or values
     * ({@code Many<T>} returns None (if {@code None}).
     *
     * @param f   function that converts {@code T} to {@code R}
     * @param <R> type that the value of a {@code Some<T>} should be converted to
     *
     * @return a new {@code MultiOption<R>} with the converted value(s)
     */
    default <R> MultiOption<R> map(Function<? super T, ? extends R> f) {
        return switch (this) {
            case None<T> ignored -> none();
            case One<T> one -> one(f.apply(one.value));
            case Many<T> many -> many(many.values.stream()
                                                 .map(f)
                                                 .collect(Collectors.toList()));
        };
    }

    /**
     * Calls the provided {@code Consumer<T>} with the contained value(s) (if {@code One<T>} or {@code Many<T>}) and returns itself. This
     * allows for chaining multiple consumers that all need the value(s) e.g.:
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
    default MultiOption<T> inspect(Consumer<? super Collection<? super T>> consumer) {
        switch (this) {
            case None<T> ignored -> {
            }
            case One<T> one -> consumer.accept(Collections.singletonList(one.value));
            case Many<T> many -> consumer.accept(many.values);
        }

        return this;
    }

    /**
     * Returns the provided default result (if {@code None<T>}), or applies a {@code Fuction<T,R>} to the contained value (if
     * {@code One}) or values (if {@code Many<T>}).
     * <p>
     * Arguments passed to {@link MultiOption#mapOr(Collection, Function)} are eagerly evaluated; if you are passing the result of a
     * function call,
     * it is recommended to use {@link MultiOption#mapOrElse(Supplier, Function)}, which is lazily evaluated.
     *
     * @param defaultValue default value that is returned if {@code None<T>}
     * @param f            function that converts the value in case of {@code One<T>} and values in case of {@code Many<T>}
     * @param <R>          the target type that should be mapped to
     *
     * @return the mapped value or the default value if {@code None<T>}
     */
    default <R> Collection<R> mapOr(Collection<R> defaultValue, Function<? super T, ? extends R> f) {
        return switch (this) {
            case None<T> ignored -> defaultValue;
            case One<T> one -> Collections.singletonList(f.apply(one.value));
            case Many<T> many -> many.values.stream().map(f).collect(Collectors.toList());
        };
    }
    /**
     * + Computes a default function result (if {@code None}), or applies a different function to the contained value (if {@code One}) or
     * values (if {@code Many<T>}).
     *
     * @param defaultSupplier {@code Supplier<? extends Collection<? extends R>>} that supplies a value(s) if {@code None}
     * @param f               function that converts the value in case of {@code Some<T>} and values of {@code Many<T>}
     * @param <R>             the target type that should be mapped to
     *
     * @return the mapped value or the default value if {@code None}
     */
    default <R> Collection<R> mapOrElse(Supplier<? extends Collection<R>> defaultSupplier, Function<? super T, ? extends R> f) {
        return switch (this) {
            case None<T> ignored -> defaultSupplier.get();
            case One<T> one -> Collections.singletonList(f.apply(one.value));
            case Many<T> many -> many.values.stream().map(f).collect(Collectors.toList());
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
     * @param unknownAmount an iterator of unknown size that should be turned into a {@link MultiOption}
     * @param <T>           type of the list elements
     *
     * @return a {@link MultiOption} according to the above rules
     */
    static <T, I extends Iterator<T>> MultiOption<T> from(I unknownAmount) {
        ArrayList<T> accumulator = new ArrayList<>();
        while (unknownAmount.hasNext()) {
            accumulator.add(unknownAmount.next());
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
     * @param unknownAmount a collection of unknown size that should be turned into a {@link MultiOption}
     * @param <T>           type of the list elements
     *
     * @return a {@link MultiOption} according to the above rules
     */
    static <T> MultiOption<T> from(Collection<T> unknownAmount) {
        if (unknownAmount == null || unknownAmount.isEmpty()) {
            return new None<>();

        } else if (unknownAmount.size() == 1) {
            var it = unknownAmount.iterator();
            if (it.hasNext()) {
                return new One<>(it.next());
            } else {
                return new None<>();
            }

        } else {
            return new Many<>(unknownAmount);
        }
    }

    static <T> MultiOption<T> fromMultiOptions(Collection<MultiOption<T>> multipleMultiOptions) {
        return MultiOption.from(multipleMultiOptions.stream()
                                                    .flatMap(multiOption -> switch (multiOption) {
                                                        case MultiOption.None<T> ignored -> Stream.of();
                                                        case MultiOption.One<T> one -> Stream.of(one.value());
                                                        case MultiOption.Many<T> many -> many.values()
                                                                                             .stream();
                                                    })
                                                    .collect(Collectors.toSet()));
    }

    default int size() {
        return switch (this) {
            case None<T> ignored -> 0;
            case One<T> ignored -> 1;
            case Many<T> many -> many.values.size();
        };
    }

    @SuppressWarnings("unchecked")
    static <T> MultiOption<T> none() {
        return (MultiOption<T>) NONE;
    }

    static <T> MultiOption<T> one(T single) {
        return new One<>(single);
    }

    static <T, C extends Collection<T>> MultiOption<T> many(C many) {
        return new Many<>(many);
    }
}
