package the.oronco.tuple;

import lombok.NonNull;
import lombok.With;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import the.oronco.Rusty;
import the.oronco.adt.Result;

import java.util.Collection;
import java.util.List;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
@With
@Unmodifiable
public record Pair<T0, T1>(T0 _0, T1 _1)
        implements MultiValue<Pair<T0, T1>, Pair<T1, T0>, Pair<T1, T0>>, Rusty<Collection<Object>>, Tuple.Size2<T0, T1> {
    public static <T0, T1> @NotNull Pair<T0, T1> of(T0 v0, T1 v1) {
        return new Pair<>(v0, v1);
    }

    public static <T0, T1> @NotNull Pair<T0, T1> of(@NotNull @NonNull Tuple.Size2<T0, T1> other) {
        return Pair.of(other._0(), other._1());
    }

    public static <T> @NotNull Result<Pair<T, T>, TupleError> from(
            @NonNull @NotNull final T[] vals) {
        if (vals.length > SIZE) {
            return Result.err(new TupleError.CreateError.TooFewElements(SIZE, vals.length, 0));
        } else if (vals.length < SIZE) {
            return Result.err(new TupleError.CreateError.TooManyElements(SIZE, vals.length, 0));
        }
        return Result.ok(Pair.of(vals[0], vals[1]));
    }

    public static <T> @NotNull Result<Pair<T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip) {
        return from(iterable, numberOfElementsToSkip, false);
    }

    public static <T> @NotNull Result<Pair<T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable) {
        return from(iterable, 0, true);
    }

    private static <T> @NotNull Result<Pair<T, T>, TupleError> from(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip, boolean exactSize) {

        return Tuples.extractValues(iterable, numberOfElementsToSkip, exactSize, SIZE)
                     .map(vals -> Pair.of(vals[0], vals[1]))
                     .mapErr(e -> e);
    }

    @Override
    public @NotNull Collection<Object> j() {
        return List.of(_0, _1);
    }

    @Override
    public @NotNull Pair<T1, T0> rotL() {
        return new Pair<>(_1, _0);
    }

    @Override
    public @NotNull Pair<T1, T0> rotR() {
        return new Pair<>(_1, _0);
    }
}
