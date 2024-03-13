package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

import java.util.function.Predicate;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SkipWhile<T, P extends Predicate<? super T>> implements Iter<T> {
    private final Iter<T> iter;
    private final P predicate;

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
