package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public final class Enumerate<T> implements Iter<T> {
    private final Iter<T> tIter;

    @Override
    public Option<T> next() {
        return null;
    }
}
