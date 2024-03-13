package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Inspect< T, F extends Consumer<? super T>> implements Iter<T> {
    private final Iter<T> iter;
    private final F f;

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
