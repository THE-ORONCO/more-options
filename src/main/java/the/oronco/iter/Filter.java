package the.oronco.iter;

import java.util.Objects;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor
public final class Filter<T, P extends Predicate<? super T>> implements Iter<T> {
    private final Iter<T> iter;
    private final P predicate;

    @Override
    public Option<T> next() {
        return null;
    }
}
