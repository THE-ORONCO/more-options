package the.oronco.iter;

import lombok.SneakyThrows;
import the.oronco.adt.ControlFlow;
import the.oronco.adt.ControlFlow.Break;
import the.oronco.adt.ControlFlow.Continue;
import the.oronco.adt.Option;
import the.oronco.adt.Result;
import the.oronco.adt.Try;
import the.oronco.tuple.Twople;

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
public interface Iter<T> extends IntoIter<T, Iter<T>> {
    Option<T> next();

    default Result<T[], Iter<T>> nextChunk(int size) {
        // TODO
        return null;
    }

    default Twople<Integer, Option<Integer>> sizeHint() {
        return new Twople<>(0, Option.none());
    }

    default int count() {
        return this.fold(0, (count, ignored) -> count + 1);
    }

    // TODO create a size type and a non zero integer type
    default Result<Object, Integer> advanceBy(int n) {
        for (int i = 0; i < n; i++) {
            if (this.next()
                    .isNone()) {
                return Result.err(n - i);
            }
        }

        return Result.ok(new Object()); // TODO find better solution
    }

    default Option<T> nth(int n) {
        if (this.advanceBy(n)
                .ok()
                .isNone()) {
            return Option.none();
        }
        return this.next();
    }

    default Option<T> last() {
        return this.fold(Option.none(), (Option<T> ignored, T t) -> Option.some(t));
    }

    default StepBy<T> stepBy(int step) {
        return new StepBy<>(this, step);
    }

    default <U extends IntoIter<T, Iter<T>>> Chain<T, Iter<T>, Iter<T>> chain(U other) {
        return new Chain<>(this, other.intoIter());
    }

    default <U extends IntoIter<T, Iter<T>>> Zip<T, Iter<T>, Iter<T>> zip(U other) {
        return new Zip<>(this, other.intoIter());
    }

    default <U extends T> Intersperse<T, Iter<T>> intersperse(U separator) {
        return new Intersperse<>(new Peekable<>(this), separator);
    }

    default <E extends T, G extends Supplier<E>> IntersperseWith<T, E, G, Iter<T>> intersperseWith(G separator) {
        return new IntersperseWith<>(this, separator);
    }

    default <B> Mapi<T, ? extends Iter<T>, Function<? super T, B>, B> map(Function<? super T, B> f) {
        return new Mapi<>(this, f);
    }

    default <F extends Consumer<? super T>> void forEach(F f) {
        BiFunction<Void, T, Void> consumer = (ignored, value) -> {
            f.accept(value);
            return null;
        };
        this.fold(null, consumer);
    }

    default <P extends Predicate<? super T>> Filter<T, P> filter(P predicate) {
        return new Filter<>(this, predicate);
    }

    default <B, F extends Function<? super T, Option<B>>> FilterMap<B, T, F> filterMap(F f) {
        return new FilterMap<>(this, f);
    }

    default Enumerate<T> enumerate() {
        return new Enumerate<>(this);
    }

    default Peekable<T> peekable() {
        return new Peekable<>(this);
    }

    default <P extends Predicate<? super T>> SkipWhile<T, P> skipWhile(P predicate) {
        return new SkipWhile<>(this, predicate);
    }

    default <P extends Predicate<? super T>> TakeWhile<T, P> takeWhile(P predicate) {
        return new TakeWhile<>(this, predicate);
    }

    default <B, P extends Function<? super T, Option<B>>> MapWhile<B, T, P> mapWhile(P predicate) {
        return new MapWhile<>(this, predicate);
    }

    default Skip<T> skip(int n) {
        return new Skip<>(this, n);
    }

    default Take<T> take(int n) {
        return new Take<>(this, n);
    }

    default <S, F extends BiFunction<S, T, Option<B>>, B> Scan<T, S, F, B> scan(S initialState, F f) {
        return new Scan<>(this, initialState, f);
    }

    default <R, J extends Iter<R>, F extends Function<? super T, ? extends IntoIter<R, J>>> FlatMap<T, Iter<T>, R, J, F> flatMap(F f) {
        return new FlatMap<>(this, f);
    }

    default Flatten<T> flatten() {
        return new Flatten<>(this);
    }

    // unstable
//    default <I extends Iter<T>, F extends Function<? extends List<T>, R>, R> MapWindows<T, I, F, R> mapWindows(int n, F f){
//        return new MapWindows<T, I , F, R >(this, n, f);
//    }

    default Fuse<T> fuse() {
        return new Fuse<>(this);
    }

    default <F extends Consumer<? super T>> Inspect<T, F> inspect(F f) {
        return new Inspect<>(this, f);
    }

    // probably not possible :(
//    default <A, I extends Iter<A>, O extends IntoIter<A,I>, S extends FromIter<A,I,O,S>> FromIter<A,I,O,S> collect(I intoIter){
//        return FromIter
//    }
// probably not possible
//    default <B extends Extend<T>, F extends Predicate<T>> Twople<B, B> partition(F f){
//
//        B left = null;
//        B right = null;
//    }


    default <B, E, R extends Try<B, E>> R tryFold(final B init, BiFunction<B, T, R> f) {
        B accum = init;
        R value = null; // workaround for javas typ system
        while (this.next() instanceof Option.Some<T> some) {
            value = f.apply(accum, some.value());
            switch (value.branch()) {
                case ControlFlow.Continue<E, B> cntu -> accum = cntu.c();
                case ControlFlow.Break<E, B> ignored -> {
                    return value;
                }
            }
        }

        return value;
    }

    default <E, R extends Try<Void, E>> R tryForEach(Function<T, R> f) {
        final Function<Function<T, R>, BiFunction<Void, T, R>> call = (fi) -> (ignored, x) -> fi.apply(x);

        return this.tryFold(null, call.apply(f));
    }

    @SneakyThrows
    default <B> B fold(B init, BiFunction<? super B, ? super T, ? extends B> f) {
        B accum = init;
        var a = this.next();
        while (a.isSome()) {
            accum = f.apply(accum,
                            this.next()
                                .unwrap());
        }
        return accum;
    }

    // check if the supers and extends here are necessary
    default <F extends BiFunction<? super T, ? super T, ? extends T>> Option<T> reduce(F f) {
        var first = this.next();
        return switch (first) {
            case Option.Some<T> some -> Option.some(this.fold(some.value(), f));
            case Option.None<T> none -> none;
        };
    }

    default <R extends Try<T, Option<T>>> Option<T> tryReduce(BiFunction<T, T, R> f) {
        T first;
        switch (this.next()) {
            case Option.Some<T> some -> first = some.value();
            case Option.None<T> none -> {
                return none;
            }
        }
        ;

        R a = this.tryFold(first, f);
        return switch (a.branch()) {
            case ControlFlow.Continue<Option<T>, T> ctnu -> Option.some(ctnu.c());
            case ControlFlow.Break<Option<T>, T> brk -> brk.b();
        };
    }

    default <F extends Predicate<T>> boolean all(F f) {
        BiFunction<Void, T, ControlFlow<Void, Void>> check = (ignored, x) -> {
            if (f.test(x)) {
                return new Continue<>(null);
            } else {
                return new Break<>(null);
            }
        };
        return this.tryFold(null, check)
                   .equals(new Continue<>(null));
    }

    default <F extends Predicate<T>> boolean any(F f) {
        BiFunction<Void, T, ControlFlow<Void, Void>> check = (ignored, x) -> {
            if (f.test(x)) {
                return new Break<>(null);
            } else {
                return new Continue<>(null);
            }
        };
        return this.tryFold(null, check)
                   .equals(new Break<>(null));
    }

    default <P extends Predicate<T>> Option<T> find(P predicate) {
        Function<Predicate<T>, BiFunction<Void, T, ControlFlow<T, Void>>> check = p -> (ignored, x) -> {
            if (p.test(x)) {
                return new Break<>(x);
            } else {
                return new Continue<>(null);
            }
        };
        return this.tryFold(null, check.apply(predicate))
                   .breakValue();
    }

    default <B, F extends Function<T, Option<B>>> Option<B> findMap(F f) {
        Function<Function<T, Option<B>>, BiFunction<Void, T, ControlFlow<B, Void>>> check = p -> (ignored1, x) -> switch (p.apply(x)) {
            case Option.Some<B> some -> new Break<>(some.value());
            case Option.None<B> ignored2 -> new Continue<>(null);
        };
        return this.tryFold(null, check.apply(f))
                   .breakValue();
    }

    default <R extends Try<Boolean, Option<T>>> Option<T> tryFind(Function<T, R> f) {
        Function<Function<T, R>, BiFunction<Void, T, ControlFlow<Option<T>, Void>>> check = (Function<T, R> fun) -> (ignored, x) -> switch (fun.apply(
                                                                                                                                                       x)
                                                                                                                                               .branch()) {
            case ControlFlow.Continue<Option<T>, Boolean> ctnu when !ctnu.c() -> new Continue<>(null);
            case ControlFlow.Continue<Option<T>, Boolean> ignored1 when true -> new Break<>(Option.some(x));
            case ControlFlow.Break<Option<T>, Boolean> r -> new Break<>(r.b());
        };

        return switch (this.tryFold(null, check.apply(f))) {
            case ControlFlow.Break<Option<T>, Void> brk -> brk.b();
            case ControlFlow.Continue<Option<T>, Void> ignored -> Option.none();
        };
    }

    default <P extends Predicate<T>> Option<Integer> position(P predicate) {
        Function<P, BiFunction<Integer, T, ControlFlow<Integer, Integer>>> check = (p) -> (i, x) -> {
            if (p.test(x)) {
                return new Break<>(i);
            } else {
                return new Continue<>(i + 1);
            }
        };

        return this.tryFold(0, check.apply(predicate))
                   .breakValue();
    }

    default <B extends Comparable<B>> Option<T> maxByKey(Function<T, B> keyGenerator) {
        Function<Function<T, B>, Function<T, Twople<B, T>>> key = (f) -> x -> new Twople<>(f.apply(x), x);

        BiFunction<Twople<B, T>, Twople<B, T>, Integer> compare = (x, y) -> x.a()
                                                                             .compareTo(y.a());

        return switch (this.map(key.apply(keyGenerator))
                           .maxBy(compare)) {
            case Option.Some<Twople<B, T>> some -> Option.some(some.value()
                                                                   .b());
            case Option.None<Twople<B, T>> ignored -> Option.none();
        };
    }


    default Option<T> maxBy(BiFunction<T, T, Integer> compare) {
        Function<BiFunction<T, T, Integer>, BiFunction<T, T, T>> fold = (c) -> (x, y) -> switch (c.apply(x, y)) {
            case Integer i when i <= 0 -> y;
            case Integer ignored when true -> x;
        };

        return reduce(fold.apply(compare));
    }

    default <B extends Comparable<B>> Option<T> minByKey(Function<T, B> keyGenerator) {
        Function<Function<T, B>, Function<T, Twople<B, T>>> key = (f) -> x -> new Twople<>(f.apply(x), x);

        BiFunction<Twople<B, T>, Twople<B, T>, Integer> compare = (x, y) -> x.a()
                                                                             .compareTo(y.a());

        return switch (this.map(key.apply(keyGenerator))
                           .minBy(compare)) {
            case Option.Some<Twople<B, T>> some -> Option.some(some.value()
                                                                   .b());
            case Option.None<Twople<B, T>> ignored -> Option.none();
        };
    }


    default Option<T> minBy(BiFunction<T, T, Integer> compare) {
        Function<BiFunction<T, T, Integer>, BiFunction<T, T, T>> fold = (c) -> (x, y) -> switch (c.apply(x, y)) {
            case Integer i when i <= 0 -> x;
            case Integer ignored when true -> y;
        };

        return reduce(fold.apply(compare));
    }


    default Cycle<T, Iter<T>> cycle() {
        return new Cycle<>(this);
    }

    default ArrayChunks<T, Iter<T>> array_chunk(int n) {
        return new ArrayChunks<>(this, n);
    }

    default <U, I extends IntoIter<U, Iter<U>>, F extends BiFunction<T, U, Integer>> Integer cmpBy(I other, F cmp) {
        Function<BiFunction<T, U, Integer>, BiFunction<T, U, ControlFlow<Integer, Void>>> compare = (c) -> (x, y) -> switch (c.apply(x,
                                                                                                                                     y)) {
            case Integer i when i == 0 -> new Continue<>(null);
            case Integer nonEq -> new Break<>(nonEq);
        };

        return switch (iterCompare(this, other.intoIter(), compare.apply(cmp))) {
            case ControlFlow.Continue<Integer, Integer> ord -> ord.c();
            case ControlFlow.Break<Integer, Integer> ord -> ord.b();
        };
    }

    default <U, I extends IntoIter<U, Iter<U>>, F extends BiFunction<T, U, Option<Integer>>> Option<Integer> partialCmpBy(I other, F cmp) {
        Function<BiFunction<T, U, Option<Integer>>, BiFunction<T, U, ControlFlow<Option<Integer>, Void>>> compare = (c) -> (x, y) -> switch (c.apply(
                x,
                y)) {
            case Option.Some<Integer> some when some.value() == 0 -> new Continue<>(null);
            case Option<Integer> nonEq -> new Break<>(nonEq);
        };

        return switch (iterCompare(this, other.intoIter(), compare.apply(cmp))) {
            case ControlFlow.Continue<Option<Integer>, Integer> ord -> Option.some(ord.c());
            case ControlFlow.Break<Option<Integer>, Integer> ord -> ord.b();
        };
    }


    default <U, I extends IntoIter<U, Iter<U>>, F extends BiPredicate<T, U>> boolean eqBy(I other, F eq) {
        Function<BiPredicate<T, U>, BiFunction<T, U, ControlFlow<Void, Void>>> compare = (eqi) -> (x, y) -> {
            if (eqi.test(x, y)) {
                return new Continue<>(null);
            } else {
                return new Break<>(null);
            }
        };

        return switch (iterCompare(this, other.intoIter(), compare.apply(eq))) {
            case ControlFlow.Continue<Void, Integer> ord -> ord.c() == 0; // TODO use custom Enum for equality
            case ControlFlow.Break<Void, Integer> ignored -> false;
        };
    }

    default <F extends BiFunction<T, T, Option<Integer>>> boolean isSortedBy(F compare) {
        Option<T> next = this.next();

        if (next instanceof Option.None<T>) {
            return true;

        } else if (next instanceof Option.Some<T> nextSome) {
            final Object[] last = new Object[]{nextSome.value()};
            BiFunction<T, BiFunction<T, T, Option<Integer>>, Predicate<T>> check = (lasti, compari) -> (curr) -> {

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

    static <A, B, T> ControlFlow<T, Integer> iterCompare(Iter<A> a, Iter<B> b, BiFunction<A, B, ControlFlow<T, Void>> f) {
        BiFunction<Iter<B>, BiFunction<A, B, ControlFlow<T, Void>>, Function<A, ControlFlow<ControlFlow<T, Integer>, Void>>> compare = (bi, fi) -> (x) -> {
            switch (bi.next()) {
                case Option.None<B> ignored -> {
                    return new Break<>(new Continue<>(1)); // TODO implement custom Order to avoid hard coding ints
                }
                case Option.Some<B> some -> {
                    return fi.apply(x, some.value())
                             .mapBreak((Function<T, ControlFlow<T, Integer>>) Break::new);
                }
            }

        };

        return switch (a.tryForEach(compare.apply(b, f))) {
            case ControlFlow.Continue<ControlFlow<T, Integer>, Void> ignored -> new Continue<>(switch (b.next()) {
                case Option.None<B> ignored2 -> 0;
                case Option.Some<B> ignored3 -> -1;
            });
            case ControlFlow.Break<ControlFlow<T, Integer>, Void> v -> v.b();
        };
    }


    @Override
    default Iter<T> intoIter() {
        return this;
    }
}
