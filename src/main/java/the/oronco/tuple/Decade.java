package the.oronco.tuple;

import lombok.NonNull;
import lombok.With;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import the.oronco.adt.Result;
import the.oronco.tuple.TupleError.CreateError.TooFewElements;
import the.oronco.tuple.TupleError.CreateError.TooManyElements;

import java.util.Collection;
import java.util.List;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
@With
@Unmodifiable
public record Decade<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9>(T0 _0, T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7, T8 _8, T9 _9)
        implements MultiValue<Decade<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9>, Decade<T1, T2, T3, T4, T5, T6, T7, T8, T9, T0>, Decade<T9, T0, T1, T2
        , T3, T4, T5, T6, T7, T8>>, Tuple.Size10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> {

    public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> @NotNull Decade<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> of(
            T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9) {
        return new Decade<>(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9);
    }

    public static <T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> @NotNull Decade<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> of(
            @NotNull @NonNull Tuple.Size10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> other) {
        return Decade.of(other._0(), other._1(), other._2(), other._3(), other._4(), other._5(), other._6(), other._7(), other._8(), other._9());
    }

    public static <T> @NotNull Result<Decade<T, T, T, T, T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final T[] vals) {
        if (vals.length > SIZE) {
            return Result.err(new TooFewElements(SIZE, vals.length, 0));
        } else if (vals.length < SIZE) {
            return Result.err(new TooManyElements(SIZE, vals.length, 0));
        }
        return Result.ok(Decade.of(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5], vals[6], vals[7], vals[8], vals[9]));
    }

    public static <T> @NotNull Result<Decade<T, T, T, T, T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip) {
        return from(iterable, numberOfElementsToSkip, false);
    }

    public static <T> @NotNull Result<Decade<T, T, T, T, T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable) {
        return from(iterable, 0, true);
    }

    private static <T> @NotNull Result<Decade<T, T, T, T, T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip, boolean exactSize) {

        return Tuples.extractValues(iterable, numberOfElementsToSkip, exactSize, SIZE)
                     .map(vals -> Decade.of(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5], vals[6], vals[7], vals[8], vals[9]))
                     .mapErr(e -> e);
    }

    @Override
    public @NotNull Collection<Object> j() {
        return List.of(_0, _1, _2, _3, _4, _5, _6, _7, _8, _9);
    }

    @Override
    public @NotNull Decade<T1, T2, T3, T4, T5, T6, T7, T8, T9, T0> rotL() {
        return new Decade<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _0);
    }

    @Override
    public @NotNull Decade<T9, T0, T1, T2, T3, T4, T5, T6, T7, T8> rotR() {
        return new Decade<>(_9, _0, _1, _2, _3, _4, _5, _6, _7, _8);
    }

    @Override
    public String toString() {
        return "(" + _0 + ", " + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ", " + _6 + ", " + _7 + ", " + _8 + ", " + _9 + ')';
    }
}
