package the.oronco.iter;

import the.oronco.adt.Option;

/**
 * @author the_oronco@posteo.net
 * @since 11/02/2024
 */
public class Fuse<T> implements Iter<T> {

    private Option<Iter<T>> iter;

    protected Fuse(Iter<T> iter) {
        this.iter = Option.some(iter);
    }

    @Override
    public Option<T> next() {
        return null;
    }
}