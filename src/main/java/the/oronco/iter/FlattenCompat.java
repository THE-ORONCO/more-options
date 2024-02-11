package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

/**
 * @author the_oronco@posteo.net
 * @since 11/02/2024
 */
public class FlattenCompat<T> implements Iter<T> {
    private final Fuse<T> iter;
    private Option<T> frontiter;
    private Option<T> backiter;

    protected FlattenCompat(Iter<T> iter){
        this.iter = iter.fuse();
        this.backiter = Option.none();
        this.frontiter = Option.none();
    }

    @Override
    public Option<T> next() {
        return null;
    }
}
