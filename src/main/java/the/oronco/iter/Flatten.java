package the.oronco.iter;

import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

/**
 * @author the_oronco@posteo.net
 * @since 11/02/2024
 */
public class Flatten<T> implements Iter<T> {
    private final FlattenCompat<T> inner;

    protected Flatten(Iter<T> inner) {
        this.inner = new FlattenCompat<>(inner);}

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
