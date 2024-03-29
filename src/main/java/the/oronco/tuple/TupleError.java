package the.oronco.tuple;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
public sealed interface TupleError {
    sealed interface IndexError extends TupleError {
        record IndexTooLargeError(int requestedIndex, int maxIndex) implements IndexError {}

        record IndexSmallerZeroError(int requestedIndex) implements IndexError {}
    }
    sealed interface CreateError extends TupleError {
        record CannotSkipANegativeAmountOfElements(long negativeSkipValue) implements CreateError {
        }

        record TooFewElements(long expected, long actual, long skipped) implements CreateError {
        }

        record TooManyElements(long expected, long actual, long skipped) implements CreateError {
        }

        record SkippedToManyElements(long skipped, long actualNumber) implements CreateError {
        }

    }

}
