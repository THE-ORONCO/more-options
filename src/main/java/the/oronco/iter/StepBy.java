package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public final class StepBy<T> implements Iter<T> {
    private final Iter<T> tIter;
    private final int step;

    @Override
    public Option<T> next() {
        return null;
    }
}
