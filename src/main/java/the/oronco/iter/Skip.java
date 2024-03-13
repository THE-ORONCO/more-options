package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Skip<T> implements Iter<T>{
    private final Iter<T> iter;
    private final int n;

    @Override
    public Option<T> next() {
        return null;
    }
}