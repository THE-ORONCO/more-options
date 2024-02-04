package the.oronco.iter;

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public final class IntersperseWith<T, E extends T, G extends Supplier<E>, I extends Iter<T>> implements Iter<T> {
    private final I iter;
    private final G separator;

    @Override
    public Option<T> next() {
        return null;
    }
}
