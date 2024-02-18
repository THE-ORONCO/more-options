package the.oronco.adt;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Describes a control flow through the type system. This allows (if the compiler is clever which in java it isn't) for highly optimized
 * code as execution paths might be determined at compile time.
 *
 * @author the_oronco@posteo.net
 * @since 11/02/2024
 */
public sealed interface ControlFlow<B, C> extends Try<C, ControlFlow<B, Infallible>> {
    @Override
    default ControlFlow<ControlFlow<B, Infallible>, C> branch() {
        return switch (this) {
            case ControlFlow.Continue<B, C> cntu -> new Continue<>(cntu.c);
            case ControlFlow.Break<B, C> brk -> new Break<>(new Break<>(brk.b));
        };
    }

    record Break<B, C>(B b) implements ControlFlow<B, C> {}

    record Continue<B, C>(C c) implements ControlFlow<B, C> {}

    default boolean isBreak() {
        return switch (this) {
            case ControlFlow.Break<B, C> ignored -> true;
            case ControlFlow.Continue<B, C> ignored -> false;
        };
    }

    default boolean isContinue() {
        return switch (this) {
            case ControlFlow.Break<B, C> ignored -> false;
            case ControlFlow.Continue<B, C> ignored -> true;
        };
    }

    default Option<B> breakValue() {
        return switch (this) {
            case ControlFlow.Break<B, C> brk -> Option.some(brk.b);
            case ControlFlow.Continue<B, C> ignored -> Option.none();
        };
    }

    default <T, F extends Function<B, T>> ControlFlow<T, C> mapBreak(F f) {
        return switch (this) {
            case ControlFlow.Break<B, C> brk -> new Break<>(f.apply(brk.b));
            case ControlFlow.Continue<B, C> cntu -> new Continue<>(cntu.c);
        };
    }

    default Option<C> continueValue() {
        return switch (this) {
            case ControlFlow.Break<B, C> ignored -> Option.none();
            case ControlFlow.Continue<B, C> cntu -> Option.some(cntu.c);
        };
    }

    default <T, F extends Function<C, T>> ControlFlow<B, T> mapContinue(F f) {
        return switch (this) {
            case ControlFlow.Break<B, C> brk -> new Break<>(brk.b);
            case ControlFlow.Continue<B, C> cntu -> new Continue<>(f.apply(cntu.c));
        };
    }
}
