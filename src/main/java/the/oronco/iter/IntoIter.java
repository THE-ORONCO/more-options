package the.oronco.iter;

import org.jetbrains.annotations.NotNull;

public interface IntoIter<T, I extends Iter<@NotNull T>> {
    @NotNull I intoIter();
}
