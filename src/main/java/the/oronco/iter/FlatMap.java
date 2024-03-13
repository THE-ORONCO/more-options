package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

import java.util.function.Function;

/**
 * @author the_oronco@posteo.net
 * @since 05/02/2024
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FlatMap<T, I extends Iter<T>, R, J extends Iter<R>, F extends Function<? super T, ? extends IntoIter<R, J>>> implements Iter<R>{
    private I iter;
    private F f;

    @Override
    public @NotNull Option<R> next() {
        return null;
    }
}
