package the.oronco.adt.funcs;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
@FunctionalInterface
public interface NotNullPredicate<T> extends Predicate<@NotNull T> {
    @Override
    boolean test(@NotNull T t);

    @NotNull
    @Override
    default Predicate<@NotNull T> and(@NotNull Predicate<? super @NotNull T> other) {
        return t -> this.test(t) && other.test(t);
    }

    @NotNull
    @Override
    default Predicate<@NotNull T> negate() {
        return t -> !this.test(t);
    }

    @NotNull
    @Override
    default Predicate<@NotNull T> or(@NotNull Predicate<? super @NotNull T> other) {
        return t -> this.test(t) || other.test(t);
    }
}
