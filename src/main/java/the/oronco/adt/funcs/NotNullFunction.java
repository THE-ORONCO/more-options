package the.oronco.adt.funcs;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
@FunctionalInterface
public interface NotNullFunction<T, R> extends Function<@NotNull T, @NotNull R> {
    @Override
    @NotNull R apply(@NotNull T t);


    @Override
    default <V> @NotNull Function<V, @NotNull R> compose(@NotNull @NonNull Function<? super @NotNull V, ? extends @NotNull T> before) {
        return v -> this.apply(before.apply(v));
    }

    @Override
    default <V> @NotNull Function<@NotNull T, V> andThen(@NotNull @NonNull Function<? super @NotNull R, ? extends @NotNull V> after) {
        return t -> after.apply(this.apply(t));
    }
}
