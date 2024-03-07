package the.oronco.tuple;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @param <R> type of the right shifted tuple
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
public interface RightRotation<R extends RightRotation<?>> {
    // TODO create method that accepts int for number of rotations (i.e. rotR(int n))
    @Contract(value = "-> new")
    @NotNull R rotR();
}
