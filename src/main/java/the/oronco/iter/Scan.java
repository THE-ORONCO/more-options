package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

import java.util.function.BiFunction;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Scan<T, S, F extends BiFunction<? super @NotNull S, ? super @NotNull T, @NotNull Option<B>>, B> implements Iter<T> {
    private final Iter<T> iter;
    private final S state;
    private final F f;

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
