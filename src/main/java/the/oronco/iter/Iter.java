package the.oronco.iter;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import the.oronco.adt.ControlFlow;
import the.oronco.adt.ControlFlow.Break;
import the.oronco.adt.ControlFlow.Continue;
import the.oronco.adt.Option;
import the.oronco.adt.Result;
import the.oronco.adt.Try;
import the.oronco.tuple.Twople;

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


    default <B, E, R extends Try<B, E>> R tryFold(final B init, BiFunction<B, ? super T, R> f) {
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

    default <E, F extends Function<? super T, Result<Void, E>>> Result<Void, E> tryForEach(F f) {
        final BiFunction<Void, ? super T, Result<Void, E>> call = (ignored, x) -> f.apply(x);
        return this.tryFold(null, call);
    }

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

    default <R extends Try<T, Option<T>>> Option<T> tryReduce(BiFunction<T, ? super T, R> f) {
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


    default Cycle<T, Iter<T>> cycle(){
        return new Cycle<>(this);
    }


    @Override
    default Iter<T> intoIter() {
        return this;
    }
}
