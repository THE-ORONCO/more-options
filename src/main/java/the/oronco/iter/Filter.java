package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

import java.util.function.Predicate;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Filter<T, P extends Predicate<? super @NotNull T>> implements Iter<T> {
    private final @NotNull Iter<T> iter;
    private final @NotNull P predicate;

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
