package the.oronco.adt.nonnulllambdas;

import org.jetbrains.annotations.NotNull;

/**
 * A Supplier that contractually does not return null.
 *
 * @author the_oronco@posteo.net
 * @since 18/02/2024
 */
@FunctionalInterface
public interface NotNullSupplier<T> {
    @NotNull T get();
}