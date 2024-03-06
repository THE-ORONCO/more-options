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
public record Octet<T0, T1, T2, T3, T4, T5, T6, T7>(T0 _0, T1 _1, T2 _2, T3 _3, T4 _4, T5 _5, T6 _6, T7 _7)
        implements Tuple<Octet<T0, T1, T2, T3, T4, T5, T6, T7>, Octet<T1, T2, T3, T4, T5, T6, T7, T0>, Octet<T7, T0, T1, T2, T3, T4, T5, T6>>

        ,
                   Indexed.Value0<T0>,
                   Indexed.Value1<T1>,
                   Indexed.Value2<T2>,
                   Indexed.Value3<T3>,
                   Indexed.Value4<T4>,
                   Indexed.Value5<T5>,
                   Indexed.Value6<T6>,
                   Indexed.Value7<T7> {
    public static int SIZE = 8;

    @Override
    public int size() {
        return SIZE;
    }

    public static <T0, T1, T2, T3, T4, T5, T6, T7> @NotNull Octet<T0, T1, T2, T3, T4, T5, T6, T7> of(
            T0 v0, T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7) {
        return new Octet<>(v0, v1, v2, v3, v4, v5, v6, v7);
    }

    public static <T> @NotNull Result<Octet<T, T, T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final T[] vals) {
        if (vals.length > SIZE) {
            return Result.err(new TupleError.CreateError.TooFewElements(SIZE, vals.length, 0));
        } else if (vals.length < SIZE) {
            return Result.err(new TupleError.CreateError.TooManyElements(SIZE, vals.length, 0));
        }
        return Result.ok(Octet.of(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5], vals[6], vals[7]));
    }

    public static <T> @NotNull Result<Octet<T, T, T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip) {
        return from(iterable, numberOfElementsToSkip, false);
    }

    public static <T> @NotNull Result<Octet<T, T, T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable) {
        return from(iterable, 0, true);
    }

    private static <T> @NotNull Result<Octet<T, T, T, T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip, boolean exactSize) {

        return Tuples.extractValues(iterable, numberOfElementsToSkip, exactSize, SIZE)
                     .map(vals -> Octet.of(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5], vals[6], vals[7]))
                     .mapErr(e -> e);
    }

    @Override
    public Collection<Object> j() {
        return List.of(_0, _1, _2, _3, _4, _5, _6, _7);
    }

    @Override
    public @NotNull Octet<T1, T2, T3, T4, T5, T6, T7, T0> rotL() {
        return new Octet<>(_1, _2, _3, _4, _5, _6, _7, _0);
    }

    @Override
    public @NotNull Octet<T7, T0, T1, T2, T3, T4, T5, T6> rotR() {
        return new Octet<>(_7, _0, _1, _2, _3, _4, _5, _6);
    }
}
