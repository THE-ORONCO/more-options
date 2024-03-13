package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MapWhile<B, T, F extends Function<? super T, Option<B>>> implements Iter<T> {
    private final @NotNull @NonNull Iter<T> iter;
    private final @NotNull @NonNull F predicate;

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
