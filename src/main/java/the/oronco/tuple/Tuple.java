package the.oronco.tuple;

import java.io.Serializable;
import java.util.Collection;
import java.util.function.Function;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import the.oronco.Rusty;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
public sealed interface Tuple<T extends Tuple<T, L, R> & TupleL<L> & TupleR<R>, L extends TupleL<?>, R extends TupleR<?>>
        extends TupleR<R>, TupleL<L>, Rusty<Collection<Object>>, Serializable
        permits Unit, Pair, Triplet, Quartet, Quintet, Sextet, Septet, Octet, Ennead, Decade {

    @SuppressWarnings("unchecked") // safe because T refers to the own type when the tuples are implemented correctly
    default <O extends Tuple<O, L2, R2>, L2 extends TupleL<?>, R2 extends TupleR<?>> Tuple<O, L2, R2> map(Function<T, O> f) {
        return f.apply((T) this);
    }

    int size();

    @Override
    @Contract(value = "-> new", pure = true)
    @Unmodifiable Collection<Object> j();
}
