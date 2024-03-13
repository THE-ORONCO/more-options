package the.oronco.adt;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Describes a control flow through the type system. This allows (if the compiler is clever which in java it isn't) for highly optimized code as
 * execution paths might be determined at compile time.
 *
 * @author the_oronco@posteo.net
 * @since 11/02/2024
 */
public sealed interface ControlFlow<B, C> extends Try<C, ControlFlow<B, Infallible>> {
    @Override
    default ControlFlow<ControlFlow<B, Infallible>, C> branch() {
        return switch (this) {
            case Continue<B, C> cntu -> cntu(cntu.c);
            case Break<B, C> brk -> brk(new Break<>(brk.b));
        };
    }

    record Break<B, C>(@NotNull @NonNull B b) implements ControlFlow<@NotNull B, @NotNull C> {
    }

    record Continue<B, C>(@NotNull @NonNull C c) implements ControlFlow<@NotNull B, @NotNull C> {
    }

    default boolean isBreak() {
        return switch (this) {
            case Break<B, C> ignored -> true;
            case Continue<B, C> ignored -> false;
        };
    }

    default boolean isContinue() {
        return switch (this) {
            case Break<B, C> ignored -> false;
            case Continue<B, C> ignored -> true;
        };
    }

    default @NotNull Option<B> breakValue() {
        return switch (this) {
            case Break<B, C>(B brk) -> Option.some(brk);
            case Continue<B, C> ignored -> Option.none();
        };
    }

    default <T> ControlFlow<T, C> mapBreak(@NotNull @NonNull Function<? super @NotNull B, ? extends @NotNull T> f) {
        return switch (this) {
            case Break<B, C>(B brk) -> brk(f.apply(brk));
            case Continue<B, C>(C cntu) -> cntu(cntu);
        };
    }

    default Option<C> continueValue() {
        return switch (this) {
            case Break<B, C> ignored -> Option.none();
            case Continue<B, C>(C cntu) -> Option.some(cntu);
        };
    }

    default <T> ControlFlow<B, T> mapContinue(@NotNull @NonNull Function<? super @NotNull C, ? extends @NotNull T> f) {
        return switch (this) {
            case Break<B, C> brk -> new Break<>(brk.b);
            case Continue<B, C> cntu -> new Continue<>(f.apply(cntu.c));
        };
    }

    static <B, C> ControlFlow<B, C> brk(@NotNull @NonNull B brk) {
        return new Break<>(brk);
    }

    static <B, C> ControlFlow<B, C> cntu(@NotNull @NonNull C ctnu) {
        return new Continue<>(ctnu);
    }
}
