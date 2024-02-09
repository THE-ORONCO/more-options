package the.oronco.adt;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * @author Théo Roncoletta
 * @since 09.02.24
 **/
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Safe<T> implements AutoCloseable {
    private Result<T, Exception> currentResult;
    private Option<Finalizer> finalizer;

    public static <T> Safe<T> on(T value) {
        return new Safe<>(Result.ok(value), the.oronco.adt.Option.none());
    }
    public static <T> Safe<T> on(T value, Finalizer finalizer) {
        return new Safe<>(Result.ok(value), the.oronco.adt.Option.of(finalizer));
    }
    private static <T> Safe<T> on(Result<T, Exception> result, Option<Finalizer> finalizer) {
        return new Safe<>(result, finalizer);
    }

    public <R> Safe<R> map(Function<? super T, ? extends R> f) {
        try{
            return switch (currentResult) {
                case Result.Ok<T, Exception> ok -> Safe.on(ok.map(f), finalizer);
                case Result.Err<T, Exception> err -> Safe.on(err.as(), finalizer);
            };

        }catch (Exception e){
            return Safe.on(Result.err(e), finalizer);
        }
    }

    public Result<T, Exception> result(){
        return currentResult;
    }

    @Override
    public void close() throws Exception {
        switch (finalizer) {
            case Option.None<Finalizer> ignored -> {
            }
            case Option.Some<Finalizer> some -> some.value().fin();
        }
    }

    @FunctionalInterface
    public interface Finalizer {
        void fin();
    }
}
