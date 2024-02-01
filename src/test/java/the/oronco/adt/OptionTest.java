package the.oronco.adt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

class OptionTest {

    @Test
    void isSome() {
        var some = Option.some("something");
        assertTrue(some.isSome());
        var none = Option.none();
        assertFalse(none.isSome());
    }

    @Test
    void isNone() {
        var none = Option.none();
        assertTrue(none.isNone());
        var some = Option.some("something");
        assertFalse(some.isNone());
    }

    @Test
    void isSomeAnd() {
        var someValue = "some Stirng";
        var some = Option.some(someValue);
        assertTrue(some.isSomeAnd(value -> value.equals(someValue)));
    }

    @Test
    void streamNothing() {
        var none = Option.none();
        var stream = none.stream();
        assertTrue(stream.findAny()
                         .isEmpty());
    }

    @Test
    void streamSomething() {
        String someValue = "something";
        var some = Option.some(someValue);
        var stream = some.stream();
        Optional<String> someValueFromStream = stream.findFirst();
        assertTrue(someValueFromStream.isPresent());
        assertEquals(someValue, someValueFromStream.get());
    }

    @Test
    void expectNothing() {
        var none = Option.none();
        assertThrows(NoSuchElementException.class, () -> none.expect("Error"));
    }

    @Test
    void expectSomething() {
        String someValue = "something";
        var some = Option.some(someValue);
        var value = assertDoesNotThrow(() -> some.expect("Error"));
        assertEquals(someValue, value);
    }

    @Test
    void unwrap() {
    }

    @Test
    void unwrapOr() {
    }

    @Test
    void unwrapOrElse() {
    }

    @Test
    void map() {
    }

    @Test
    void inspect() {
    }

    @Test
    void mapOr() {
    }

    @Test
    void mapOrElse() {
    }

    @Test
    void okOr() {
    }

    @Test
    void okOrElse() {
    }

    @Test
    void iter() {
    }

    @Test
    void and() {
    }

    @Test
    void andThen() {
    }

    @Test
    void filter() {
    }

    @Test
    void or() {
    }

    @Test
    void orElse() {
    }

    @Test
    void xOr() {
    }

    @Test
    void toOptional() {
    }

    @Test
    void optionOf() {
    }

    @Test
    void some() {
    }

    @Test
    void none() {
    }
}