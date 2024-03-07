package the.oronco.tuple;

import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import the.oronco.Rusty;
import the.oronco.adt.Result;
import the.oronco.adt.funcs.ThrowingFunction;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
public sealed interface Tuple<T extends Tuple<T, L, R> & TupleL<L> & TupleR<R>, L extends TupleL<?>, R extends TupleR<?>>
        extends TupleR<R>, TupleL<L>, Rusty<Collection<Object>>, Serializable, Iterable<Object>, Indexed
        permits Unit, Pair, Triplet, Quartet, Quintet, Sextet, Septet, Octet, Ennead, Decade {

    @SuppressWarnings("unchecked") // safe because T refers to the own type when the tuples are implemented correctly
    default <O> O map(@NotNull @NonNull Function<? super @NotNull T, ? extends O> f) {
        return f.apply((T) this);
    }

    @SuppressWarnings("unchecked") // safe because T refers to the own type when the tuples are implemented correctly
    default <O, X extends Exception> Result<? extends O, X> safeMap(@NotNull @NonNull ThrowingFunction<? super @NotNull T, ? extends O, @NotNull X> f) {
        return f.apply((T) this);
    }

    int size();

    @Override
    @Contract(value = "-> new", pure = true)
    @Unmodifiable Collection<Object> j();

    @NotNull
    @Override
    default Iterator<Object> iterator() {
        return this.j()
                   .iterator();
    }
}
