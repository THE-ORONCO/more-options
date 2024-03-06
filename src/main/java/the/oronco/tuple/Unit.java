package the.oronco.tuple;

import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import lombok.With;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import the.oronco.Rusty;
import the.oronco.adt.Result;

/**
 * @author Théo Roncoletta
 * @since 05.03.24
 **/
@With
@Unmodifiable
public record Unit<T0>(T0 _0) implements Tuple<Unit<T0>, Unit<T0>, Unit<T0>>, Rusty<Collection<Object>>, Indexed.Value0<T0> {
    public static int SIZE = 1;

    @Override
    public int size() {
        return SIZE;
    }

    public static <T0> @NotNull Unit<T0> of(T0 v0) {
        return new Unit<>(v0);
    }

    public static <T> @NotNull Result<Unit<T>, TupleError> from(@NonNull @NotNull final T[] vals) {
        if (vals.length > SIZE) {
            return Result.err(new TupleError.CreateError.TooFewElements(SIZE, vals.length, 0));
        } else if (vals.length < SIZE) {
            return Result.err(new TupleError.CreateError.TooManyElements(SIZE, vals.length, 0));
        }
        return Result.ok(Unit.of(vals[0]));
    }

    public static <T> @NotNull Result<Unit<T>, TupleError> from(@NonNull @NotNull final Iterable<T> iterable,
                                                                long numberOfElementsToSkip) {
        return from(iterable, numberOfElementsToSkip, false);
    }

    public static <T> @NotNull Result<Unit<T>, TupleError> from(@NonNull @NotNull final Iterable<T> iterable) {
        return from(iterable, 0, true);
    }

    private static <T> @NotNull Result<Unit<T>, TupleError> from(@NonNull @NotNull final Iterable<T> iterable,
                                                                 long numberOfElementsToSkip,
                                                                 boolean exactSize) {

        return Tuples.extractValues(iterable, numberOfElementsToSkip, exactSize, SIZE)
                     .map(vals -> Unit.of(vals[0]))
                     .mapErr(e -> e);
    }

    @Override
    public @NotNull Collection<Object> j() {
        return List.of(_0);
    }

    @Override
    public @NotNull Unit<T0> rotL() {
        return new Unit<>(_0);
    }

    @Override
    public @NotNull Unit<T0> rotR() {
        return new Unit<>(_0);
    }

    public <T1> @NotNull Pair<T0, T1> extend(T1 v1) {
        return Pair.of(this._0, v1);
    }

    public <T1> @NotNull Pair<T0, T1> extend(Unit<T1> other) {
        return this.extend(other._0);
    }

    public <T1, T2> @NotNull Triplet<T0, T1, T2> extend(T1 v1, T2 v2) {
        return Triplet.of(this._0, v1, v2);
    }

    public <T1, T2> @NotNull Triplet<T0, T1, T2> extend(Pair<T1, T2> pair) {
        return Triplet.of(this._0, pair._0(), pair._1());
    }
}
