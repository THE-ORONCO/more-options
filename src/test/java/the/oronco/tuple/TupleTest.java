package the.oronco.tuple;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import the.oronco.adt.Option;
import the.oronco.adt.Result;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Théo Roncoletta
 * @since 14.03.24
 **/
class TupleTest {
    @ParameterizedTest
    @MethodSource("tuplesWithSizes")
    void testSizes(Tuple tuple, int size) {
        assertEquals(size, tuple.size());
    }

    @ParameterizedTest
    @MethodSource("tuplesWithSizes")
    void testGet(Tuple tuple, int size) {

        assertAll(IntStream.range(0, size)
                           .mapToObj(i -> (Executable) () -> {
                               var result = tuple.get(i);
                               switch (result) {
                                   case Result.Ok<Option<Object>, TupleError.IndexError>(var value) -> value.inspect(v -> assertEquals(i, v));
                                   case Result.Err<Option<Object>, TupleError.IndexError>(var ignored) -> fail(
                                           "Could not get value at index %d in tuple %s!".formatted(i, tuple));
                               }
                           })
                           .toList());
    }

    @ValueSource
    static Stream<Arguments> tuplesWithSizes() {
        return Stream.of(
                Arguments.of(Empty.of(), 0),
                Arguments.of(Unit.of(0), 1),
                Arguments.of(Pair.of(0, 1), 2),
                Arguments.of(Triplet.of(0, 1, 2), 3),
                Arguments.of(Quartet.of(0, 1, 2, 3), 4),
                Arguments.of(Quintet.of(0, 1, 2, 3, 4), 5),
                Arguments.of(Sextet.of(0, 1, 2, 3, 4, 5), 6),
                Arguments.of(Septet.of(0, 1, 2, 3, 4, 5, 6), 7),
                Arguments.of(Octet.of(0, 1, 2, 3, 4, 5, 6, 7), 8),
                Arguments.of(Ennead.of(0, 1, 2, 3, 4, 5, 6, 7, 8), 9),
                Arguments.of(Decade.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), 10));
    }
}
