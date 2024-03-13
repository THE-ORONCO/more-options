package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

import java.util.function.Supplier;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IntersperseWith<T, E extends T, G extends Supplier<? extends @NotNull E>, I extends Iter<T>> implements Iter<T> {
    private final @NotNull I iter;
    private final @NotNull G separator;

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
