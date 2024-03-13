package the.oronco.tuple;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.StreamUtils;
import the.oronco.adt.Result;

import java.util.Iterator;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
@UtilityClass
public class Tuples {
    static <T> Result<T[], TupleError.CreateError> extractValues(
            @NonNull @NotNull final Iterable<T> iterable, long numberOfElementsToSkip, boolean exactsize, int size) {
        if (numberOfElementsToSkip < 0) {
            return Result.err(new TupleError.CreateError.CannotSkipANegativeAmountOfElements(numberOfElementsToSkip));
        }

        Iterator<T> iter = iterable.iterator();

        for (long currentElement = 0; currentElement < numberOfElementsToSkip; currentElement++) {
            if (iter.hasNext()) {
                iter.next();
            } else if (exactsize) {
                return Result.err(new TupleError.CreateError.SkippedToManyElements(numberOfElementsToSkip, currentElement));
            }
        }

        @SuppressWarnings("unchecked")
        T[] vals = (T[]) new Object[size];
        for (int i = 0; i < size; i++) {
            if (iter.hasNext()) {
                vals[i] = iter.next();
            } else if (exactsize) {
                return Result.err(new TupleError.CreateError.TooFewElements(size, i, numberOfElementsToSkip));
            }
        }

        if (exactsize && iter.hasNext()) {
            long remaining = StreamUtils.createStreamFromIterator(iter)
                                        .count();
            return Result.err(new TupleError.CreateError.TooManyElements(size, numberOfElementsToSkip + size + remaining, numberOfElementsToSkip));
        }

        return Result.ok(vals);
    }
}
