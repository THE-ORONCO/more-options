package the.oronco.iter;

import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

/**
 * @author the_oronco@posteo.net
 * @since 11/02/2024
 */
public class ArrayChunks<T, I extends Iter<T>> implements Iter<T> {
    private final I iter;
    private final int n;
    private Option<IntoIter<T, I>> remainder;

    protected ArrayChunks(I iter, int n) {
        this.iter = iter;
        this.n = n;
        this.remainder = Option.none();
    }

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
