package the.oronco.iter;

import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Inspect< T, F extends Consumer<? super T>> implements Iter<T> {
    private final Iter<T> iter;
    private final F f;

    @Override
    public Option<T> next() {
        return null;
    }
}
