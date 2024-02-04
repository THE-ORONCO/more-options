package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public final class Intersperse<T, I extends Iter<T>> implements Iter<T> {
    private final I iter;
    private final T separator;

    @Override
    public Option<T> next() {
        return null;
    }
}
