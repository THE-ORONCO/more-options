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
public sealed interface MultiValue<T extends MultiValue<T, L, R> & LeftRotation<L> & RightRotation<R>, L extends LeftRotation<?>,
        R extends RightRotation<?>>
        extends RightRotation<R>, LeftRotation<L>, Rusty<Collection<Object>>, Serializable, Iterable<Object>, Tuple
        permits Decade, Ennead, Octet, Pair, Quartet, Quintet, Septet, Sextet, Triplet, Unit, Empty {

    @SuppressWarnings("unchecked") // safe because T refers to the own type when the tuples are implemented correctly
    default <O> O map(@NotNull @NonNull Function<? super @NotNull T, ? extends O> f) {
        return f.apply((T) this);
    }

    @SuppressWarnings("unchecked") // safe because T refers to the own type when the tuples are implemented correctly
    default <O, X extends Exception> Result<? extends O, X> safeMap(@NotNull @NonNull ThrowingFunction<? super @NotNull T, ? extends O, @NotNull X> f) {
        return f.apply((T) this);
    }

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
