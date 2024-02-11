package the.oronco.iter;

import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Scan<T, S, F extends BiFunction<S, T, Option<B>>, B> implements Iter<T> {
    private final Iter<T> iter;
    private final S state;
    private final F f;

    @Override
    public Option<T> next() {
        return null;
    }
}
