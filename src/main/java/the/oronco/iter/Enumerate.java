package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Enumerate<T> implements Iter<T> {
    private final Iter<T> tIter;

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
