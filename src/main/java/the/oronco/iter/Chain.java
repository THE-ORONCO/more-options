package the.oronco.iter;

import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor
public final class Chain<T, A extends Iter<T>, B extends Iter<T>> implements Iter<T> {
    private final A a;
    private final B b;

    @Override
    public Option<T> next() {
        return null;
    }
}
