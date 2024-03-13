package the.oronco.iter;

import org.jetbrains.annotations.NotNull;
import the.oronco.adt.Option;

public class Intersperse<T, I extends Iter<T>> implements Iter<T> {
    private final @NotNull Peekable<T> iter;
    private final @NotNull T separator;
    private boolean needsSep;

    protected Intersperse(I iter, T separator) {
        this.iter = new Peekable<>(iter);
        this.separator = separator;
        this.needsSep = false;
    }


    @Override
    public @NotNull Option<T> next() {
        if (this.needsSep && this.iter.peek()
                                      .isSome()) {
            this.needsSep = false;
            return Option.some(this.separator); // todo somehow enforce cloning
        } else {
            this.needsSep = true;
            return this.iter.next();
        }
    }
}
