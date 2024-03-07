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
public record Triplet<T0, T1, T2>(T0 _0, T1 _1, T2 _2)
        implements MultiValue<Triplet<T0, T1, T2>, Triplet<T1, T2, T0>, Triplet<T2, T0, T1>>, Tuple.Size3<T0, T1, T2> {
    public static <T0, T1, T2> @NotNull Triplet<T0, T1, T2> of(T0 v0, T1 v1, T2 v2) {
        return new Triplet<>(v0, v1, v2);
    }

    public static <T0, T1, T2> @NotNull Triplet<T0, T1, T2> of(@NotNull @NonNull Tuple.Size3<T0, T1, T2> other) {
        return Triplet.of(other._0(), other._1(), other._2());
    }

    public static <T> @NotNull Result<Triplet<T, T, T>, TupleError> from(
            @NonNull @NotNull final T[] vals) {
        if (vals.length > SIZE) {
            return Result.err(new TupleError.CreateError.TooFewElements(SIZE, vals.length, 0));
        } else if (vals.length < SIZE) {
            return Result.err(new TupleError.CreateError.TooManyElements(SIZE, vals.length, 0));
        }
        return Result.ok(Triplet.of(vals[0], vals[1], vals[2]));
    }

    public static <T> @NotNull Result<Triplet<T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip) {
        return from(iterable, numberOfElementsToSkip, false);
    }

    public static <T> @NotNull Result<Triplet<T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable) {
        return from(iterable, 0, true);
    }

    private static <T> @NotNull Result<Triplet<T, T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip, boolean exactSize) {

        return Tuples.extractValues(iterable, numberOfElementsToSkip, exactSize, SIZE)
                     .map(vals -> Triplet.of(vals[0], vals[1], vals[2]))
                     .mapErr(e -> e);
    }

    @Override
    public @NotNull Collection<Object> j() {
        return List.of(_0, _1, _2);
    }

    @Override
    public @NotNull Triplet<T1, T2, T0> rotL() {
        return new Triplet<>(_1, _2, _0);
    }

    @Override
    public @NotNull Triplet<T2, T0, T1> rotR() {
        return new Triplet<>(_2, _0, _1);
    }
}
