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
 * @since 05.03.24
 **/
@With
@Unmodifiable
public record Unit<T0>(T0 _0) implements MultiValue<Unit<T0>, Unit<T0>, Unit<T0>>, Tuple.Size1<T0> {
    public static <T0> @NotNull Unit<T0> of(T0 v0) {
        return new Unit<>(v0);
    }

    public static <T0> @NotNull Unit<T0> of(@NotNull @NonNull Tuple.Size1<T0> other) {
        return Unit.of(other._0());
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
}
