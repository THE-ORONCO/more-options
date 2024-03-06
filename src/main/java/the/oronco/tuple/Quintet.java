package the.oronco.tuple;

import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import lombok.With;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import the.oronco.adt.Result;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
@With
@Unmodifiable
public record Quintet<T0, T1, T2, T3, T4>(T0 _0, T1 _1, T2 _2, T3 _3, T4 _4)
        implements Tuple<Quintet<T0, T1, T2, T3, T4>, Quintet<T1, T2, T3, T4, T0>, Quintet<T4, T0, T1, T2, T3>>,
                   Indexed.Value4<T0, T1, T2, T3, T4> {
    public static int SIZE = 5;

    @Override
    public int size() {
        return SIZE;
    }

    public static <T0, T1, T2, T3, T4> @NotNull Quintet<T0, T1, T2, T3, T4> of(T0 v0, T1 v1, T2 v2, T3 v3, T4 v4) {
        return new Quintet<>(v0, v1, v2, v3, v4);
    }

    public static <T> @NotNull Result<Quintet<T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final T[] vals) {
        if (vals.length > SIZE) {
            return Result.err(new TupleError.CreateError.TooFewElements(SIZE, vals.length, 0));
        } else if (vals.length < SIZE) {
            return Result.err(new TupleError.CreateError.TooManyElements(SIZE, vals.length, 0));
        }
        return Result.ok(Quintet.of(vals[0], vals[1], vals[2], vals[3], vals[4]));
    }

    public static <T> @NotNull Result<Quintet<T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip) {
        return from(iterable, numberOfElementsToSkip, false);
    }

    public static <T> @NotNull Result<Quintet<T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable) {
        return from(iterable, 0, true);
    }

    private static <T> @NotNull Result<Quintet<T, T, T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip, boolean exactSize) {

        return Tuples.extractValues(iterable, numberOfElementsToSkip, exactSize, SIZE)
                     .map(vals -> Quintet.of(vals[0], vals[1], vals[2], vals[3], vals[4]))
                     .mapErr(e -> e);
    }

    @Override
    public @NotNull Collection<Object> j() {
        return List.of(_0, _1, _2, _3, _4);
    }

    @Override
    public @NotNull Quintet<T1, T2, T3, T4, T0> rotL() {
        return new Quintet<>(_1, _2, _3, _4, _0);
    }

    @Override
    public @NotNull Quintet<T4, T0, T1, T2, T3> rotR() {
        return new Quintet<>(_4, _0, _1, _2, _3);
    }
}
