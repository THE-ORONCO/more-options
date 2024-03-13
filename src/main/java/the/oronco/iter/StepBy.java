package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class StepBy<T> implements Iter<T> {
    private final Iter<T> tIter;
    private final int step;

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
