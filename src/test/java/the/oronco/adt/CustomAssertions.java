package the.oronco.adt;

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;

import lombok.experimental.UtilityClass;
import the.oronco.adt.funcs.ThrowingFunction;
import the.oronco.adt.funcs.ThrowingSupplier;
import the.oronco.tuple.Empty;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Théo Roncoletta
 * @since 14.03.24
 **/
@UtilityClass
public class CustomAssertions {
    private static <T, A, B, E extends Exception, F extends Exception> T assertClosureCalledThrowing(
            ThrowingFunction<A, B, E> closure,
            ThrowingFunction<ThrowingFunction<A, B, E>, ? extends T, F> thingToTest,
            Supplier<String> messageSupplier,
            boolean called) throws Exception {

        AtomicBoolean closureWasCalled = new AtomicBoolean(false);

        Closeable assertion = () -> {
            if (closureWasCalled.get() != called) {
                assertionFailure().message(messageSupplier)
                                  .expected(called)
                                  .actual(!called)
                                  .buildAndThrow();
            }
        };
        try (assertion) {
            return thingToTest.applyThrowing(o -> {
                closureWasCalled.set(true);
                return closure.applyThrowing(o);
            });
        }
    }

    private static <T, B, E extends Exception, F extends Exception> T assertClosureCalledThrowing(
            ThrowingSupplier<B, E> closure,
            ThrowingFunction<ThrowingSupplier<B, E>, T, F> thingToTest,
            Supplier<String> messageSupplier,
            boolean called) throws Exception {

        return assertClosureCalledThrowing(ignored -> closure.get(),
                                           func -> thingToTest.applyThrowing(() -> func.applyThrowing(Empty.of())),
                                           messageSupplier,
                                           called);
    }

    private static <T, A, B> T assertClosureCalled(
            Function<A, B> closure, Function<Function<A, B>, T> thingToTest, Supplier<String> messageSupplier, boolean called) {
        try {
            return assertClosureCalledThrowing(closure::apply, f -> thingToTest.apply(a -> {
                try {
                    return f.applyThrowing(a);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }), messageSupplier, called);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T, B> T assertClosureCalled(
            Supplier<B> closure, Function<Supplier<B>, T> thingToTest, Supplier<String> messageSupplier, boolean called) {
        try {
            return assertClosureCalledThrowing(closure::get, f -> thingToTest.apply(() -> {
                try {
                    return f.getThrows();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }), messageSupplier, called);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    static <T, A, B, E extends Exception, F extends Exception> T assertClosureCalledThrowing(
            ThrowingFunction<A, B, E> closure, ThrowingFunction<ThrowingFunction<A, B, E>, T, F> thingToTest, Supplier<String> messageSupplier)
            throws Exception {
        return assertClosureCalledThrowing(closure, thingToTest, messageSupplier, true);
    }

    static <T, A, B> T assertClosureCalled(
            Function<A, B> closure, Function<Function<A, B>, T> thingToTest, Supplier<String> messageSupplier) {
        return assertClosureCalled(closure, thingToTest, messageSupplier, true);
    }

    static <T, A, B, E extends Exception, F extends Exception> T assertClosureCalledThrowing(
            ThrowingFunction<A, B, E> closure, ThrowingFunction<ThrowingFunction<A, B, E>, T, F> thingToTest, String message) throws Exception {
        return assertClosureCalledThrowing(closure, thingToTest, () -> message);
    }

    static <T, A, B> T assertClosureCalled(
            Function<A, B> closure, Function<Function<A, B>, T> thingToTest, String message) {
        return assertClosureCalled(closure, thingToTest, () -> message);
    }

    static <T, A, B, E extends Exception, F extends Exception> T assertClosureCalledThrowing(
            ThrowingFunction<A, B, E> closure, ThrowingFunction<ThrowingFunction<A, B, E>, T, F> thingToTest) throws Exception {
        return assertClosureCalledThrowing(closure,
                                           thingToTest,
                                           "Expected the closure 1 to be called by the other closure 2, when closure 2 was supplied with closure 1!");
    }

    static <T, A, B> T assertClosureCalled(
            Function<A, B> closure, Function<Function<A, B>, T> thingToTest) {
        return assertClosureCalled(closure,
                                   thingToTest,
                                   "Expected the closure 1 to be called by the other closure 2, when closure 2 was supplied with closure 1!");
    }

    static <T, A, E extends Exception, F extends Exception> T assertClosureCalledThrowing(
            ThrowingSupplier<A, E> closure, ThrowingFunction<ThrowingSupplier<A, E>, T, F> thingToTest, Supplier<String> messageSupplier)
            throws Exception {
        return assertClosureCalledThrowing(closure, thingToTest, messageSupplier, true);
    }

    static <T, A, B, E extends Exception, F extends Exception> T assertClosureCalledThrowing(
            ThrowingSupplier<A, E> closure, ThrowingFunction<ThrowingSupplier<A, E>, T, F> thingToTest, String message) throws Exception {
        return assertClosureCalledThrowing(closure, thingToTest, () -> message);
    }


    static <T, A, E extends Exception, F extends Exception> T assertClosureCalledThrowing(
            ThrowingSupplier<A, E> closure, ThrowingFunction<ThrowingSupplier<A, E>, T, F> thingToTest) throws Exception {
        return assertClosureCalledThrowing(closure,
                                           thingToTest,
                                           "Expected the closure 1 to be called by the other closure 2, when closure 2 was supplied with closure 1!");
    }

    static <T, A, B, E extends Exception, F extends Exception> T assertClosureNotCalledThrowing(
            ThrowingFunction<A, B, E> closure, ThrowingFunction<ThrowingFunction<A, B, E>, T, F> thingToTest, Supplier<String> messageSupplier)
            throws Exception {
        return assertClosureCalledThrowing(closure, thingToTest, messageSupplier, false);
    }

    static <T, A, E extends Exception, F extends Exception> T assertClosureNotCalledThrowing(
            ThrowingSupplier<A, E> closure, ThrowingFunction<ThrowingSupplier<A, E>, T, F> thingToTest, Supplier<String> messageSupplier)
            throws Exception {
        return assertClosureCalledThrowing(closure, thingToTest, messageSupplier, false);
    }

    static <T, A, E extends Exception, F extends Exception> T assertClosureNotCalledThrowing(
            ThrowingSupplier<A, E> closure, ThrowingFunction<ThrowingSupplier<A, E>, T, F> thingToTest, String message) throws Exception {
        return assertClosureNotCalledThrowing(closure, thingToTest, () -> message);
    }

    static <T, A, B, E extends Exception, F extends Exception> T assertClosureNotCalledThrowing(
            ThrowingFunction<A, B, E> closure, ThrowingFunction<ThrowingFunction<A, B, E>, T, F> thingToTest, String message) throws Exception {
        return assertClosureNotCalledThrowing(closure, thingToTest, () -> message);
    }

    static <T, A, B, E extends Exception, F extends Exception> T assertClosureNotCalledThrowing(
            ThrowingFunction<A, B, E> closure, ThrowingFunction<ThrowingFunction<A, B, E>, T, F> thingToTest) throws Exception {
        return assertClosureNotCalledThrowing(closure,
                                              thingToTest,
                                              "Expected the closure 1 NOT to be called by the other closure 2, when closure 2 was supplied with "
                                              + "closure 1!");
    }

    static <T, A, E extends Exception, F extends Exception> T assertClosureNotCalledThrowing(
            ThrowingSupplier<A, E> closure, ThrowingFunction<ThrowingSupplier<A, E>, T, F> thingToTest) throws Exception {
        return assertClosureNotCalledThrowing(closure,
                                              thingToTest,
                                              "Expected the closure 1 NOT to be called by the other closure 2, when closure 2 was supplied with "
                                              + "closure 1!");
    }

    static <T, A, B> T assertClosureNotCalled(
            Function<A, B> closure, Function<Function<A, B>, T> thingToTest, Supplier<String> messageSupplier) {
        return assertClosureCalled(closure, thingToTest, messageSupplier, false);
    }

    static <T, A> T assertClosureNotCalled(
            Supplier<A> closure, Function<Supplier<A>, T> thingToTest, Supplier<String> messageSupplier) {
        return assertClosureCalled(closure, thingToTest, messageSupplier, false);
    }


    static <T, A, E extends Exception, F extends Exception> T assertClosureNotCalled(
            Supplier<A> closure, Function<Supplier<A>, T> thingToTest, String message) {
        return assertClosureNotCalled(closure, thingToTest, () -> message);
    }

    static <T, A, B, E extends Exception, F extends Exception> T assertClosureNotCalled(
            Function<A, B> closure, Function<Function<A, B>, T> thingToTest, String message) {
        return assertClosureNotCalled(closure, thingToTest, () -> message);
    }

    static <T, A, B, E extends Exception, F extends Exception> T assertClosureNotCalled(
            Function<A, B> closure, Function<Function<A, B>, T> thingToTest) {
        return assertClosureNotCalled(closure,
                                      thingToTest,
                                      "Expected the closure 1 NOT to be called by the other closure 2, when closure 2 was supplied with "
                                      + "closure 1!");
    }

    static <T, A, E extends Exception, F extends Exception> T assertClosureNotCalled(
            Supplier<A> closure, Function<Supplier<A>, T> thingToTest) {
        return assertClosureNotCalled(closure,
                                      thingToTest,
                                      "Expected the closure 1 NOT to be called by the other closure 2, when closure 2 was supplied with "
                                      + "closure 1!");
    }
}
