package the.oronco.adt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static the.oronco.adt.CustomAssertions.assertClosureCalled;
import static the.oronco.adt.CustomAssertions.assertClosureCalledThrowing;
import static the.oronco.adt.CustomAssertions.assertClosureNotCalled;
import static the.oronco.adt.CustomAssertions.assertClosureNotCalledThrowing;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import the.oronco.adt.exceptions.UnwrappedBadValueException;
import the.oronco.adt.funcs.ThrowingFunction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

class OptionTest {

    @Nested
    class TestBooleanOperations {
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
            var someValue = "some String";
            var some = Option.some(someValue);
            assertTrue(some.isSomeAnd(value -> value.equals(someValue)));

            var none = Option.<String>none();
            assertFalse(none.isSomeAnd(value -> value.equals(someValue)));
        }
    }

    @Nested
    class TestStreaming {
        @Test
        void streamNothing() {
            var none = Option.none();
            var stream = none.stream();
            assertTrue(stream.findAny()
                             .isEmpty());
        }

        @Test
        void streamSomething() {
            var someValue = "something";
            var some = Option.some(someValue);
            var stream = some.stream();
            var someValueFromStream = stream.findFirst();
            assertTrue(someValueFromStream.isPresent());
            assertEquals(someValue, someValueFromStream.get());
        }

        @Test
        void streamSizeExpectations() {
            var someValue = "something";
            var some = Option.some(someValue);
            var stream = some.stream();
            var size = stream.count();
            assertEquals(1, size);
        }
    }

    @Nested
    class TestUnwrapping {

        @Nested
        class WithNoArgument {
            @Test
            void unwrapNothing() {
                var none = Option.none();
                assertThrows(UnwrappedBadValueException.class, none::unwrap);
            }

            @Test
            void unwrapSomething() {
                var someValue = "something";
                var some = Option.some(someValue);
                var value = assertDoesNotThrow(() -> some.unwrap());
                assertEquals(someValue, value);
            }
        }

        @Nested
        class WithErrorString {
            @Test
            void unwrapNothingWithMessage() {
                var message = "Error";
                var none = Option.none();
                var exception = assertThrows(UnwrappedBadValueException.class, () -> none.unwrap(message));
                assertEquals(message, exception.getMessage());
            }

            @Test
            void unwrapSomethingWithMessage() {
                var message = "Error";
                var someValue = "something";
                var some = Option.some(someValue);
                var value = assertDoesNotThrow(() -> some.unwrap(message));
                assertEquals(someValue, value);
            }
        }


        @Nested
        class WithException {


            @Test
            void unwrapNothingWithException() {
                var message = "Error";
                var exception = new TestException(message);
                var none = Option.none();
                var thrownException = assertThrows(TestException.class, () -> none.unwrap(exception));
                assertEquals(exception, thrownException);
                assertEquals(exception.getMessage(), thrownException.getMessage());
            }

            @Test
            void unwrapSomethingWithMessage() {
                var message = "Error";
                var exception = new TestException(message);
                var someValue = "something";
                var some = Option.some(someValue);
                var value = assertDoesNotThrow(() -> some.unwrap(exception));
                assertEquals(someValue, value);
            }

            @Test
            void unwrapNothingWithExceptionClosure() {
                var message = "Error";
                var none = Option.none();
                var thrownException = assertThrows(TestException.class,
                                                   () -> assertClosureCalledThrowing(() -> new TestException(message), none::unwrapElse));

                assertEquals(message, thrownException.getMessage());
            }

            @Test
            void unwrapSomethingWithMessageClosure() {

                var someValue = "something";
                var some = Option.some(someValue);
                var value = assertDoesNotThrow(() -> assertClosureNotCalledThrowing(() -> new TestException("Error"), some::unwrapElse));

                assertEquals(someValue, value);
            }
        }

        @Nested
        class DefaultValue {
            @Test
            void unwrapNothingWithDefault() {
                var defaultValue = "default";
                var none = Option.none();
                var returnedValue = none.unwrapOr(defaultValue);
                assertEquals(defaultValue, returnedValue);
            }

            @Test
            void unwrapSomethingWithDefault() {
                var someValue = "something";
                var defaultValue = "default";
                var none = Option.some(someValue);
                var returnedValue = none.unwrapOr(defaultValue);
                assertEquals(someValue, returnedValue);
            }

            @Test
            void unwrapNothingWithDefaultClosure() {
                var defaultValue = "default";
                AtomicBoolean closureWasCalled = new AtomicBoolean(false);
                Supplier<String> defaulter = () -> {
                    closureWasCalled.set(true);
                    return defaultValue;
                };

                var none = Option.none();
                var returnedValue = none.unwrapOrElse(defaulter);

                assertEquals(defaultValue, returnedValue);
                assertTrue(closureWasCalled.get());
            }

            @Test
            void unwrapSomethingWithDefaultClosure() {
                var defaultValue = "default";
                AtomicBoolean closureWasCalled = new AtomicBoolean(false);
                Supplier<String> defaulter = () -> {
                    closureWasCalled.set(true);
                    return defaultValue;
                };

                var someValue = "something";
                var none = Option.some(someValue);
                var returnedValue = none.unwrapOrElse(defaulter);

                assertEquals(someValue, returnedValue);
                assertFalse(closureWasCalled.get());
            }
        }
    }

    @Nested
    class TestMapping {
        @Test
        void mapNone() {
            var none = Option.none();

            var result = assertClosureNotCalled(Object::toString, none::map);

            assertInstanceOf(Option.None.class, none);
            assertSame(result, none);
            assertEquals(result, none);
        }

        @Test
        void mapSome() throws UnwrappedBadValueException {
            var someValue = 0;
            var some = Option.some(someValue);

            var result = assertClosureCalled((Function<Integer, Integer>) i -> i + 1, some::map);

            assertInstanceOf(Option.Some.class, some);
            assertNotSame(result, some);
            assertEquals(1, result.unwrap());
        }

        @Test
        void mapSomeIdentity() throws UnwrappedBadValueException {
            var someValue = 0;
            var some = Option.some(someValue);

            var result = assertClosureCalled(i -> i, some::map);

            assertInstanceOf(Option.Some.class, some);
            assertEquals(result, some);
            assertNotSame(result, some);
            assertEquals(someValue, result.unwrap());
        }
    }


    @Nested
    class TestSafeMapping {
        @Test
        void safeMapNoneSuccess() {
            var none = Option.none();

            var result = assertDoesNotThrow(() -> assertClosureNotCalledThrowing(Object::toString, none::safeMap));

            assertInstanceOf(Option.None.class, none);
            assertSame(result, none);
            assertEquals(result, none);
        }

        @Test
        void safeMapNoneFailure() {
            var errorMessage = "Error";
            var none = Option.none();

            var result = assertDoesNotThrow(() -> assertClosureNotCalledThrowing((ThrowingFunction<Object, String, TestException>) object -> {
                throw new TestException(errorMessage);
            }, none::safeMap));

            assertInstanceOf(Option.None.class, none);
            assertSame(result, none);
            assertEquals(result, none);
        }

        @Test
        void safeMapSomeSuccess() throws UnwrappedBadValueException {
            var someValue = 0;
            var some = Option.some(someValue);

            var result = assertDoesNotThrow(() -> assertClosureCalledThrowing((ThrowingFunction<Integer, Integer, TestException>) i -> i + 1,
                                                                              some::safeMap));

            assertInstanceOf(Option.Some.class, some);

            assertNotEquals(result, some);
            assertNotSame(result, some);
            assertEquals(1, result.unwrap());
        }

        @Test
        void safeMapSomeFailure() {
            var errorMessage = "Error";
            var someValue = 0;
            var some = Option.some(someValue);

            var result = assertDoesNotThrow(() -> assertClosureCalledThrowing((ThrowingFunction<Integer, Integer, TestException>) i -> {
                throw new TestException(errorMessage);
            }, some::safeMap));
            assertInstanceOf(Option.Some.class, some);

            assertInstanceOf(Option.None.class, result);
        }
    }

    private static class TestException extends Exception {
        public TestException(String message) {
            super(message);
        }
    }
}
