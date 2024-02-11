package the.oronco.iter;

import the.oronco.adt.Option;

/**
 * @author the_oronco@posteo.net
 * @since 11/02/2024
 */

public class Cycle<T, I extends Iter<T>> implements Iter<T> {

    private final I orig;
    private final I iter;

    protected Cycle(I iter) {
        this.orig = iter; // TODO somehow clone
        this.iter = iter;
    }

    @Override
    public Option<T> next() {
        return null;
    }
}
