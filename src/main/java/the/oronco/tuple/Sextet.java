package the.oronco.tuple;

import lombok.NonNull;
import lombok.With;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import the.oronco.adt.Result;

import java.util.Collection;
import java.util.List;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
@With
@Unmodifiable
public record Sextet<T0, T1, T2, T3, T4, T5>(T0 _0, T1 _1, T2 _2, T3 _3, T4 _4, T5 _5)
        implements MultiValue<Sextet<T0, T1, T2, T3, T4, T5>, Sextet<T1, T2, T3, T4, T5, T0>, Sextet<T5, T0, T1, T2, T3, T4>>, Tuple.Size6<T0, T1,
        T2, T3, T4, T5> {

    public static <T0, T1, T2, T3, T4, T5> @NotNull Sextet<T0, T1, T2, T3, T4, T5> of(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5) {
        return new Sextet<>(v0, v1, v2, v3, v4, v5);
    }

    public static <T0, T1, T2, T3, T4, T5> @NotNull Sextet<T0, T1, T2, T3, T4, T5> of(@NotNull @NonNull Tuple.Size6<T0, T1, T2, T3, T4, T5> other) {
        return Sextet.of(other._0(), other._1(), other._2(), other._3(), other._4(), other._5());
    }

    public static <T> @NotNull Result<Sextet<T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final T[] vals) {
        if (vals.length > SIZE) {
            return Result.err(new TupleError.CreateError.TooFewElements(SIZE, vals.length, 0));
        } else if (vals.length < SIZE) {
            return Result.err(new TupleError.CreateError.TooManyElements(SIZE, vals.length, 0));
        }
        return Result.ok(Sextet.of(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]));
    }

    public static <T> @NotNull Result<Sextet<T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip) {
        return from(iterable, numberOfElementsToSkip, false);
    }

    public static <T> @NotNull Result<Sextet<T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable) {
        return from(iterable, 0, true);
    }

    private static <T> @NotNull Result<Sextet<T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip, boolean exactSize) {

        return Tuples.extractValues(iterable, numberOfElementsToSkip, exactSize, SIZE)
                     .map(vals -> Sextet.of(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]))
                     .mapErr(e -> e);
    }

    @Override
    public @NotNull Collection<Object> j() {
        return List.of(_0, _1, _2, _3, _4, _5);
    }

    @Override
    public @NotNull Sextet<T1, T2, T3, T4, T5, T0> rotL() {
        return new Sextet<>(_1, _2, _3, _4, _5, _0);
    }

    @Override
    public @NotNull Sextet<T5, T0, T1, T2, T3, T4> rotR() {
        return new Sextet<>(_5, _0, _1, _2, _3, _4);
    }
}
