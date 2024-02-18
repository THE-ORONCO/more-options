package the.oronco.adt.nonnulllambdas;

import org.jetbrains.annotations.NotNull;

/**
 * A Predicate that contractually has a not null input argument.
 *
 * @author the_oronco@posteo.net
 * @since 18/02/2024
 */
@FunctionalInterface
public interface NotNullPredicate<T> {
    boolean test(@NotNull T t);
}
