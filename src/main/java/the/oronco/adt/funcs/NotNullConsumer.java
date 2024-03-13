package the.oronco.adt.funcs;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
@FunctionalInterface
public interface NotNullConsumer<T> extends Consumer<@NotNull T> {
    @Override
    void accept(@NotNull T t);


    @Override
    default @NotNull Consumer<@NotNull T> andThen(@NotNull @NonNull Consumer<? super @NotNull T> after) {
        return t -> {
            this.accept(t);
            after.accept(t);
        };
    }
}
