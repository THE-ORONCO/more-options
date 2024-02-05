package the.oronco.iter;

import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MapWhile<B, T, F extends Function<? super T, Option<B>>> implements Iter<T> {
    private final Iter<T> iter;
    private final F predicate;

    @Override
    public Option<T> next() {
        return null;
    }
}