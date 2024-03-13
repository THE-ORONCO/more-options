package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Mapi<T, I extends Iter<? extends T>, F extends Function<? super @NotNull T, ? extends @NotNull B>, B> implements Iter<B> {
    private final @NotNull I iter;
    private final @NotNull F f;

    @Override
    public @NotNull Option<B> next() {
        return null;
    }
}
