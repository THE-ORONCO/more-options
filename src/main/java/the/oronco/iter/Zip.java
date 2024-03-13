package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Zip<T, A extends Iter<T>, B extends Iter<T>> implements Iter<T> {
    private final A a;
    private final B b;

    @Override
    public @NotNull Option<T> next() {
        return null;
    }
}
