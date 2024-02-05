package the.oronco.iter;

import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;

/**
 * @author the_oronco@posteo.net
 * @since 05/02/2024
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FlatMap<T, R, F extends Function<? super T, ? extends IntoIter<? extends R>>> implements Iter<T>{
    private Iter<T> iter;
    private F f;

    @Override
    public Option<T> next() {
        return null;
    }
}
