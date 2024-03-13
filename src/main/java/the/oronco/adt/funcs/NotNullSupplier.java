package the.oronco.adt.funcs;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
@FunctionalInterface
public interface NotNullSupplier<T> extends Supplier<@NotNull T> {
    @Override
    @NotNull T get();
}
