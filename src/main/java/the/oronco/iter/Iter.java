package the.oronco.iter;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import the.oronco.adt.Option;
import the.oronco.adt.Result;
import the.oronco.tuple.Twople;

public interface Iter<T> {
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

    default <U extends IntoIter<T>> Chain<T, Iter<T>, Iter<T>> chain(U other) {
        return new Chain<>(this, other.intoIter());
    }

    default <U extends IntoIter<T>> Zip<T, Iter<T>, Iter<T>> zip(U other) {
        return new Zip<>(this, other.intoIter());
    }

    default <U extends T> Intersperse<T, Iter<T>> intersperse(U separator) {
        return new Intersperse<>(this, separator);
    }

    default <E extends T, G extends Supplier<E>> IntersperseWith<T, E, G, Iter<T>> intersperseWith(G separator) {
        return new IntersperseWith<>(this, separator);
    }

    default <B, F extends Function<? super T, B>> Mapi<B, T, F> map(F f) {
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

    default Enumerate<T> filterMap() {
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

    default Skip<T> skip(int n){
        return new Skip<>(this, n);
    }

    default Take<T> take(int n ){
        return new Take<>(this, n);
    }


    default <B> B fold(B init, BiFunction<B, ? super T, B> f) {
        B accum = init;
        var a = this.next();
        while (a.isSome()) {
            accum = f.apply(accum,
                            this.next()
                                .unwrap());
        }
        return accum;
    }
}
