package the.oronco.iter;

import java.util.Objects;
import java.util.function.Predicate;
import the.oronco.adt.Option;

public class Peekable<T> implements Iter<T> {
    private final Iter<T> iter;
    private Option<T> peeked;

    public Peekable(Iter<T> iter) {
        this.iter = iter;
        this.peeked = Option.none();
    }

    public Option<T> peek() {
        return switch (peeked) {
            case Option.None<T> ignored -> {
                peeked = iter.next();
                yield peeked;
            }
            case Option.Some<T> some -> some;
        };
    }

    public Option<T> nextIf(Predicate<? super T> predicate) {
        var next = this.next();
        if (next instanceof Option.Some<T> some && predicate.test(some.value())) {
            return some;
        }
        assert this.peeked.isNone();
        this.peeked = next;
        return Option.none();
    }

    public Option<T> nextIfEq(T expected) {
        return this.nextIf((next -> Objects.equals(next, expected)));
    }
    public Option<T> nextIfDeepEq(T expected) {
        return this.nextIf((next -> Objects.deepEquals(next, expected)));
    }


    @Override
    public Option<T> next() {
        return switch (peeked) {
            case Option.Some<T> some -> some;
            case Option.None<T> ignored -> this.iter.next();
        };
    }
}
