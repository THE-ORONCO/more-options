package the.oronco.iter;

import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import the.oronco.adt.Option;
import the.oronco.adt.Self;

/**
 * @author the_oronco@posteo.net
 * @since 05/02/2024
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FlatMap<T, I extends Iter<T>, R, J extends Iter<R>, F extends Function<? super T, ? extends IntoIter<R, J>>> implements Iter<R>{
    private I iter;
    private F f;

    @Override
    public Option<R> next() {
        return null;
    }
}
