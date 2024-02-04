package the.oronco.iter;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor
public final class Mapi<B, T, F extends Function<? super T, B>> implements Iter<T> {
    private final Iter<T> iter;
    private final F f;

    @Override
    public Option<T> next() {
        return null;
    }
}
