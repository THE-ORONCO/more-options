package the.oronco.iter;

import java.util.Objects;
import the.oronco.adt.Option;

public final class Peekable<T> implements Iter<T> {
    private final Iter<T> tIter;
    private Option<Option<T>> peeked;

    public Peekable(Iter<T> tIter) {
        this.tIter = tIter;
        this.peeked = Option.none();
    }


    @Override
    public Option<T> next() {
        return null;
    }
}
