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
public record Quartet<T0, T1, T2, T3>(T0 _0, T1 _1, T2 _2, T3 _3)
        implements MultiValue<Quartet<T0, T1, T2, T3>, Quartet<T1, T2, T3, T0>, Quartet<T3, T0, T1, T2>>, Tuple.Size4<T0, T1, T2, T3> {

    public static <T0, T1, T2, T3> @NotNull Quartet<T0, T1, T2, T3> of(T0 v0, T1 v1, T2 v2, T3 v3) {
        return new Quartet<>(v0, v1, v2, v3);
    }

    public static <T0, T1, T2, T3> @NotNull Quartet<T0, T1, T2, T3> of(@NotNull @NonNull Tuple.Size4<T0, T1, T2, T3> other) {
        return Quartet.of(other._0(), other._1(), other._2(), other._3());
    }

    public static <T> @NotNull Result<Quartet<T, T, T, T>, TupleError> from(
            @NonNull @NotNull final T[] vals) {
        if (vals.length > SIZE) {
            return Result.err(new TupleError.CreateError.TooFewElements(SIZE, vals.length, 0));
        } else if (vals.length < SIZE) {
            return Result.err(new TupleError.CreateError.TooManyElements(SIZE, vals.length, 0));
        }
        return Result.ok(Quartet.of(vals[0], vals[1], vals[2], vals[3]));
    }

    public static <T> @NotNull Result<Quartet<T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip) {
        return from(iterable, numberOfElementsToSkip, false);
    }

    public static <T> @NotNull Result<Quartet<T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable) {
        return from(iterable, 0, true);
    }

    private static <T> @NotNull Result<Quartet<T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip, boolean exactSize) {

        return Tuples.extractValues(iterable, numberOfElementsToSkip, exactSize, SIZE)
                     .map(vals -> Quartet.of(vals[0], vals[1], vals[2], vals[3]))
                     .mapErr(e -> e);
    }

    @Override
    public @NotNull Collection<Object> j() {
        return List.of(_0, _1, _2, _3);
    }

    @Override
    public @NotNull Quartet<T1, T2, T3, T0> rotL() {
        return new Quartet<>(_1, _2, _3, _0);
    }

    @Override
    public @NotNull Quartet<T3, T0, T1, T2> rotR() {
        return new Quartet<>(_3, _0, _1, _2);
    }

    @Override
    public String toString() {
        return "(" + _0 + ", " + _1 + ", " + _2 + ", " + _3 + ')';
    }
}
