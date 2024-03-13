package the.oronco.iter;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.ControlFlow;
import the.oronco.adt.Option;
import the.oronco.adt.Result;
import the.oronco.adt.Try;
import the.oronco.adt.funcs.NotNullFunction;
import the.oronco.tuple.Empty;
import the.oronco.tuple.Pair;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

// TODO implement all methods
// TODO implement the class functionalities behind the methods
// TODO extend Rusty<Iterable<T>>
// TODO tests
public interface Iter<T> extends IntoIter<@NotNull T, Iter<@NotNull T>> {
    @NotNull Option<T> next();

    default @NotNull Result<T[], Iter<T>> nextChunk(int size) {
        // TODO
        return null;
    }

    default @NotNull Pair<Integer, Option<Integer>> sizeHint() {
        return Pair.of(0, Option.none());
    }

    default int count() {
        return this.fold(0, (count, ignored) -> count + 1);
    }

    // TODO create a size type and a non zero integer type
    default @NotNull Result<Empty, Integer> advanceBy(int n) {
        for (int i = 0; i < n; i++) {
            if (this.next()
                    .isNone()) {
                return Result.err(n - i);
            }
        }

        return Result.ok(Empty.of()); // TODO find better solution
    }

    default @NotNull Option<T> nth(int n) {
        if (this.advanceBy(n)
                .ok()
                .isNone()) {
            return Option.none();
        }
        return this.next();
    }

    default @NotNull Option<T> last() {
        return this.fold(Option.none(), (Option<T> ignored, T t) -> Option.some(t));
    }

    default @NotNull StepBy<T> stepBy(int step) {
        return new StepBy<>(this, step);
    }

    default <U extends IntoIter<T, Iter<T>>> @NotNull Chain<T, Iter<T>, Iter<T>> chain(@NotNull @NonNull U other) {
        return new Chain<>(this, other.intoIter());
    }

    default <U extends IntoIter<T, Iter<T>>> @NotNull Zip<T, Iter<T>, Iter<T>> zip(@NotNull @NonNull U other) {
        return new Zip<>(this, other.intoIter());
    }

    default <U extends T> @NotNull Intersperse<T, Iter<T>> intersperse(@NotNull @NonNull U separator) {
        return new Intersperse<>(new Peekable<>(this), separator);
    }

    default <E extends T, G extends Supplier<? extends @NotNull E>> @NotNull IntersperseWith<T, E, G, Iter<T>> intersperseWith(@NotNull @NonNull G separator) {
        return new IntersperseWith<>(this, separator);
    }

    default <B, F extends Function<? super @NotNull T, ? extends @NotNull B>> @NotNull Mapi<T, Iter<T>, F, B> map(@NotNull @NonNull F f) {
        return new Mapi<>(this, f);
    }

    default void forEach(@NotNull @NonNull Consumer<? super @NotNull T> f) {
        BiFunction<Empty, T, Empty> consumer = (ignored, value) -> {
            f.accept(value);
            return Empty.of();
        };
        this.fold(Empty.of(), consumer);
    }

    default <P extends Predicate<? super @NotNull T>> @NotNull Filter<T, P> filter(@NotNull @NonNull P predicate) {
        return new Filter<>(this, predicate);
    }

    default <B, F extends NotNullFunction<? super T, Option<? extends B>>> @NotNull FilterMap<B, T, F> filterMap(@NotNull @NonNull F f) {
        return new FilterMap<>(this, f);
    }

    default @NotNull Enumerate<T> enumerate() {
        return new Enumerate<>(this);
    }

    default @NotNull Peekable<T> peekable() {
        return new Peekable<>(this);
    }

    default <P extends Predicate<? super @NotNull T>> @NotNull SkipWhile<T, P> skipWhile(@NotNull @NonNull P predicate) {
        return new SkipWhile<>(this, predicate);
    }

    default <P extends Predicate<? super @NotNull T>> @NotNull TakeWhile<T, P> takeWhile(@NotNull @NonNull P predicate) {
        return new TakeWhile<>(this, predicate);
    }

    default <B, P extends Function<? super @NotNull T, @NotNull Option<B>>> @NotNull MapWhile<B, T, P> mapWhile(@NotNull @NonNull P predicate) {
        return new MapWhile<>(this, predicate);
    }

    default Skip<T> skip(int n) {
        return new Skip<>(this, n);
    }

    default Take<T> take(int n) {
        return new Take<>(this, n);
    }

    default <S, F extends @NotNull BiFunction<? super @NotNull S, ? super @NotNull T, Option<B>>, B> @NotNull Scan<T, S, F, B> scan(
            @NotNull @NonNull S initialState, @NonNull F f) {
        return new Scan<>(this, initialState, f);
    }

    default <R, J extends Iter<R>, F extends @NotNull Function<? super @NotNull T, ? extends @NotNull IntoIter<R, J>>> @NotNull FlatMap<T, Iter<T>,
            R, J, F> flatMap(
            @NonNull F f) {
        return new FlatMap<>(this, f);
    }

    default @NotNull Flatten<T> flatten() {
        return new Flatten<>(this);
    }

    // unstable
    //    default <I extends Iter<T>, F extends Function<? extends List<T>, R>, R> MapWindows<T, I, F, R> mapWindows(int n, F f){
    //        return new MapWindows<T, I , F, R >(this, n, f);
    //    }

    default @NotNull Fuse<T> fuse() {
        return new Fuse<>(this);
    }

    default <F extends @NotNull Consumer<? super @NotNull T>> Inspect<T, F> inspect(@NonNull F f) {
        return new Inspect<>(this, f);
    }

    // probably not possible :(
    //    default <A, I extends Iter<A>, O extends IntoIter<A,I>, S extends FromIter<A,I,O,S>> FromIter<A,I,O,S> collect(I intoIter){
    //        return FromIter
    //    }
    // probably not possible
    //    default <B extends Extend<T>, F extends Predicate<T>> Pair<B, B> partition(F f){
    //
    //        B left = null;
    //        B right = null;
    //    }


    default <B, E, R extends Try<B, E>> R tryFold(
            @NotNull @NonNull B init, @NotNull @NonNull BiFunction<? super @NotNull B, ? super @NotNull T, ? extends @NotNull R> f) {
        B accum = init;
        R value = null; // workaround for javas typ system
        while (this.next() instanceof Option.Some<T> some) {
            value = f.apply(accum, some.value());
            switch (value.branch()) {
                case ControlFlow.Continue<E, B> cntu -> accum = cntu.c();
                case ControlFlow.Break<E, B>(E ignored) -> {
                    return value;
                }
                default -> throw new IllegalStateException("This should be unreachable!"); // TODO better handling
            }
        }

        return value;
    }

    default <E, R extends Try<Empty, E>, F extends Function<? super @NotNull T, ? extends @NotNull R>> R tryForEach(@NotNull @NonNull F f) {
        final Function<F, BiFunction<Empty, T, R>> call = fi -> (ignored, x) -> fi.apply(x);

        return this.tryFold(Empty.of(), call.apply(f));
    }

    @SneakyThrows
    default <B> @NotNull B fold(
            @NotNull @NonNull B init, @NotNull @NonNull BiFunction<? super @NotNull B, ? super @NotNull T, ? extends @NotNull B> f) {
        B accum = init;
        while (this.next()
                   .isSome()) {
            accum = f.apply(accum,
                            this.next()
                                .unwrap());
        }
        return accum;
    }

    // check if the supers and extends here are necessary
    default <F extends BiFunction<? super @NotNull T, ? super @NotNull T, ? extends @NotNull T>> @NotNull Option<T> reduce(
            @NotNull @NonNull F f) {
        return switch (this.next()) {
            case Option.Some<T> some -> Option.some(this.fold(some.value(), f));
            case Option.None<T> none -> none;
        };
    }

    default <R extends Try<T, Option<T>>> @NotNull Option<T> tryReduce(
            @NotNull @NonNull BiFunction<? super @NotNull T, ? super @NotNull T, ? extends @NotNull R> f) {
        T first;
        switch (this.next()) {
            case Option.Some<T>(T value) -> first = value;
            case Option.None<T> none -> {
                return none;
            }
        }

        R a = this.tryFold(first, f);
        return switch (a.branch()) {
            case ControlFlow.Continue<Option<T>, T> ctnu -> Option.some(ctnu.c());
            case ControlFlow.Break<Option<T>, T> brk -> brk.b();
        };
    }

    default boolean all(@NotNull @NonNull Predicate<? super @NotNull T> f) {
        BiFunction<Empty, T, ControlFlow<Empty, Empty>> check = (ignored, x) -> {
            if (f.test(x)) {
                return ControlFlow.cntu(Empty.of());
            } else {
                return ControlFlow.brk(Empty.of());
            }
        };
        return this.tryFold(Empty.of(), check)
                   .equals(ControlFlow.cntu(Empty.of()));
    }

    default boolean any(@NotNull @NonNull Predicate<? super @NotNull T> f) {
        BiFunction<Empty, T, ControlFlow<Empty, Empty>> check = (ignored, x) -> {
            if (f.test(x)) {
                return ControlFlow.brk(Empty.of());
            } else {
                return ControlFlow.cntu(Empty.of());
            }
        };
        return this.tryFold(Empty.of(), check)
                   .equals(ControlFlow.brk(Empty.of()));
    }

    default @NonNull <P extends Predicate<? super @NotNull T>> @NotNull Option<T> find(@NotNull @NonNull P predicate) {
        Function<P, BiFunction<Empty, T, ControlFlow<T, Empty>>> check = p -> (ignored, x) -> {
            if (p.test(x)) {
                return ControlFlow.brk(x);
            } else {
                return ControlFlow.cntu(Empty.of());
            }
        };
        return this.tryFold(Empty.of(), check.apply(predicate))
                   .breakValue();
    }

    default <B, F extends Function<? super @NotNull T, @NotNull Option<B>>> @NotNull Option<B> findMap(@NotNull @NonNull F f) {
        Function<F, BiFunction<Empty, T, ControlFlow<B, Empty>>> check = p -> (ignored1, x) -> switch (p.apply(x)) {
            case Option.Some<B> some -> ControlFlow.brk(some.value());
            case Option.None<B> ignored2 -> ControlFlow.cntu(Empty.of());
        };
        return this.tryFold(Empty.of(), check.apply(f))
                   .breakValue();
    }

    default <R extends Try<Boolean, Option<T>>, F extends Function<? super @NotNull T, ? extends @NotNull R>> @NotNull Option<T> tryFind(@NotNull @NonNull F f) {
        Function<F, BiFunction<Empty, T, ControlFlow<Option<T>, Empty>>> check = (F fun) -> (ignored, x) -> switch (fun.apply(x)
                                                                                                                       .branch()) {
            case ControlFlow.Continue<Option<T>, Boolean>(Boolean ctnu) when !ctnu -> ControlFlow.cntu(Empty.of());
            case ControlFlow.Continue<Option<T>, Boolean> ignored1 when true -> ControlFlow.brk(Option.some(x));
            case ControlFlow.Break<Option<T>, Boolean> r -> ControlFlow.brk(r.b());
        };

        return switch (this.tryFold(Empty.of(), check.apply(f))) {
            case ControlFlow.Break<Option<T>, Empty> brk -> brk.b();
            case ControlFlow.Continue<Option<T>, Empty> ignored -> Option.none();
        };
    }

    default <P extends Predicate<? super @NotNull T>> @NotNull Option<Integer> position(@NotNull @NonNull P predicate) {
        Function<P, BiFunction<Integer, T, ControlFlow<Integer, Integer>>> check = (p) -> (i, x) -> {
            if (p.test(x)) {
                return ControlFlow.brk(i);
            } else {
                return ControlFlow.cntu(i + 1);
            }
        };

        return this.tryFold(0, check.apply(predicate))
                   .breakValue();
    }

    default <B extends Comparable<B>, F extends @NotNull Function<? super @NotNull T, ? extends @NotNull B>> @NotNull Option<T> maxByKey(@NonNull F keyGenerator) {
        Function<F, Function<T, Pair<B, T>>> keyGen = f -> x -> Pair.of(f.apply(x), x);

        BiFunction<Pair<B, T>, Pair<B, T>, Integer> compare = (x, y) -> x._0()
                                                                         .compareTo(y._0());

        return switch (this.map(keyGen.apply(keyGenerator))
                           .maxBy(compare)) {
            case Option.Some<Pair<B, T>>(Pair(B ignored, T value)) -> Option.some(value);
            case Option.None<Pair<B, T>> ignored -> Option.none();
        };
    }


    default <F extends @NotNull BiFunction<? super @NotNull T, ? super @NotNull T, @NotNull Integer>> @NotNull Option<T> maxBy(@NonNull F compare) {
        Function<F, BiFunction<T, T, T>> fold = (c) -> (x, y) -> switch (c.apply(x, y)) {
            case Integer i when i <= 0 -> y;
            case Integer ignored when true -> x;
        };

        return reduce(fold.apply(compare));
    }

    default <B extends Comparable<B>, F extends @NotNull Function<? super @NotNull T, ? extends @NotNull B>> @NotNull Option<T> minByKey(@NonNull F keyGenerator) {
        Function<F, Function<T, Pair<B, T>>> keyGen = (f) -> x -> new Pair<>(f.apply(x), x);

        BiFunction<Pair<B, T>, Pair<B, T>, Integer> compare = (x, y) -> x._0()
                                                                         .compareTo(y._0());

        return switch (this.map(keyGen.apply(keyGenerator))
                           .minBy(compare)) {
            case Option.Some<Pair<B, T>>(Pair(B ignored, T value)) -> Option.some(value);
            case Option.None<Pair<B, T>> ignored -> Option.none();
        };
    }


    default <F extends @NotNull BiFunction<? super @NotNull T, ? super @NotNull T, @NotNull Integer>> @NotNull Option<T> minBy(@NonNull F compare) {
        Function<F, BiFunction<T, T, T>> fold = (c) -> (x, y) -> switch (c.apply(x, y)) {
            case Integer i when i <= 0 -> x;
            case Integer ignored when true -> y;
        };

        return reduce(fold.apply(compare));
    }


    default @NotNull Cycle<T, Iter<T>> cycle() {
        return new Cycle<>(this);
    }

    default @NotNull ArrayChunks<T, Iter<T>> array_chunk(int n) {
        return new ArrayChunks<>(this, n);
    }

    default <U, I extends IntoIter<U, Iter<U>>, F extends @NotNull BiFunction<T, U, Integer>> @NotNull Integer cmpBy(
            @NotNull @NonNull I other, @NonNull F cmp) {
        Function<BiFunction<T, U, Integer>, BiFunction<T, U, ControlFlow<Integer, Empty>>> compare = (c) -> (x, y) -> switch (c.apply(x, y)) {
            case Integer i when i == 0 -> ControlFlow.cntu(Empty.of());
            case Integer nonEq -> ControlFlow.brk(nonEq);
        };

        return switch (iterCompare(this, other.intoIter(), compare.apply(cmp))) {
            case ControlFlow.Continue<Integer, Integer>(Integer ord) -> ord;
            case ControlFlow.Break<Integer, Integer>(Integer ord) -> ord;
        };
    }

    default <U, I extends IntoIter<U, Iter<U>>, F extends @NotNull BiFunction<? super @NotNull T, ? super @NotNull U, @NotNull Option<Integer>>> Option<Integer> partialCmpBy(
            @NotNull @NonNull I other, @NonNull F cmp) {
        Function<F, BiFunction<T, U, ControlFlow<Option<Integer>, Empty>>> compare = (c) -> (x, y) -> switch (c.apply(x, y)) {
            case Option.Some<Integer>(Integer order) when order == 0 -> ControlFlow.cntu(Empty.of());
            case Option<Integer> nonEq -> ControlFlow.brk(nonEq);
        };

        return switch (iterCompare(this, other.intoIter(), compare.apply(cmp))) {
            case ControlFlow.Continue<Option<Integer>, Integer>(Integer completeResult) -> Option.some(completeResult);
            case ControlFlow.Break<Option<Integer>, Integer>(Option<Integer> result) -> result;
        };
    }


    default <U, I extends @NotNull IntoIter<U, Iter<U>>, F extends @NotNull BiPredicate<? super @NotNull T, ? super @NotNull U>> boolean eqBy(
            @NotNull @NonNull I other, @NonNull F eq) {
        Function<F, BiFunction<T, U, ControlFlow<Empty, Empty>>> compare = (eqi) -> (x, y) -> {
            if (eqi.test(x, y)) {
                return ControlFlow.cntu(Empty.of());
            } else {
                return ControlFlow.brk(Empty.of());
            }
        };

        return switch (iterCompare(this, other.intoIter(), compare.apply(eq))) {
            case ControlFlow.Continue<Empty, Integer>(Integer comparison) -> comparison == 0; // TODO use custom Enum for equality
            case ControlFlow.Break<Empty, Integer> ignored -> false;
        };
    }

    default <F extends @NotNull BiFunction<? super @NotNull T, ? super @NotNull T, Option<Integer>>> boolean isSortedBy(@NonNull F compare) {
        Option<T> next = this.next();

        if (next instanceof Option.None<T>) {
            return true;

        } else if (next instanceof Option.Some<T> nextSome) {
            final Object[] last = new Object[]{nextSome.value()};
            BiFunction<T, F, Predicate<T>> check = (lasti, compari) -> (curr) -> {

                Option<Integer> compResult = compari.apply(lasti, curr);
                if ((compResult instanceof Option.Some<Integer> some && some.value() > 0) || compResult instanceof Option.None<Integer>) {
                    return false;
                }

                last[0] = curr;
                return true;
            };

            //noinspection unchecked
            return this.all(check.apply((T) last[0], compare));
        } else {
            throw new IllegalStateException("this should be unreachable!");
        }
    }

    static <A, AI extends Iter<? extends A>, B, BI extends Iter<? extends B>, T, F extends @NotNull BiFunction<? super @NotNull A, ?
            super @NotNull B, ? extends @NotNull ControlFlow<T, Empty>>> @NotNull ControlFlow<T, Integer> iterCompare(
            @NotNull @NonNull AI a, @NotNull @NonNull BI b, @NonNull F f) {
        BiFunction<BI, F, Function<A, ControlFlow<ControlFlow<T, Integer>, Empty>>> compare = (bi, fi) -> (x) -> switch (bi.next()) {
            case Option.None<? extends B> ignored1 -> // TODO implement custom Order to avoid hard coding ints
                    ControlFlow.brk(ControlFlow.cntu(1));
            case Option.Some<? extends B> some -> fi.apply(x, some.value())
                                                    .mapBreak((Function<T, ControlFlow<T, Integer>>) ControlFlow::brk);
        };

        return switch (a.tryForEach(compare.apply(b, f))) {
            case ControlFlow.Continue<ControlFlow<T, Integer>, Empty> ignored -> ControlFlow.cntu(switch (b.next()) {
                case Option.None<? extends B> ignored2 -> 0;
                case Option.Some<? extends B> ignored3 -> -1;
            });
            case ControlFlow.Break<ControlFlow<T, Integer>, Empty> v -> v.b();
        };
    }


    @Override
    default @NotNull Iter<T> intoIter() {
        return this;
    }
}
