package the.oronco.adt.nonnulllambdas;

import org.jetbrains.annotations.NotNull;

/**
 * A Function that contractually has a not null input argument and also returns not null.
 *
 * @author the_oronco@posteo.net
 * @since 18/02/2024
 */
@FunctionalInterface
public interface NotNullMapper<T, R> {
    @NotNull R map(@NotNull T t);
}
