package the.oronco.adt.nonnulllambdas;

import org.jetbrains.annotations.NotNull;

/**
 * A Consumer that contractually has a not null input argument.
 *
 * @author the_oronco@posteo.net
 * @since 18/02/2024
 */
@FunctionalInterface
public interface NotNullConsumer<T> {
    void consume(@NotNull T t);
}
