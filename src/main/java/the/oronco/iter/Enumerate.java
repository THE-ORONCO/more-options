package the.oronco.iter;

import java.util.Objects;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor
public final class Enumerate<T> implements Iter<T> {
    private final Iter<T> tIter;

    @Override
    public Option<T> next() {
        return null;
    }
}
