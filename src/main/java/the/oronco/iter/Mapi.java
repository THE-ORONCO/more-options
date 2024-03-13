package the.oronco.iter;

import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Mapi< T, I extends Iter<T>, F extends Function<? super T, B>, B> implements Iter<B> {
    private final I iter;
    private final F f;

    @Override
    public Option<B> next() {
        return null;
    }
}