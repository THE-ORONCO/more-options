package the.oronco.iter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

import java.util.List;
import java.util.function.Function;

public class MapWindows<T, I extends Iter<T>, F extends Function<? extends List<T>, R>, R> implements Iter<T> {
    private final int n;
    private final F f;
    private final MapWindowsInner<T, I> inner;

    public MapWindows(Iter<T> iter, int n, F f) {
        this.inner = new MapWindowsInner<>(iter, n);
        this.f = f;
        this.n = n;
    }

    @Override
    public @NotNull Option<T> next() {
        return null;
    }

    private static class MapWindowsInner<T, I extends Iter<T>> {
        private final int n;
        private Option<I> iter;
        private Option<Buffer<T>> buffer;

        protected MapWindowsInner(Iter<T> iter, int n){
//            this.iter = Option.some(iter);
            this.buffer = Option.none();
            this.n=n;
        }
    }

    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    private static class Buffer<T> {
        private final T[][] buffer;
        private final int start;
    }
}
