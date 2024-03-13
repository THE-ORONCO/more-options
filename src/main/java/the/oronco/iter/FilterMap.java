package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FilterMap<B, T, F extends Function<? super T, Option<? extends B>>> implements Iter<T> {
    private final Iter<T> iter;
    private final F f;

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
