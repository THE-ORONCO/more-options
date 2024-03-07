package the.oronco.tuple;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @param <L> type of the right shifted tuple
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
public interface LeftRotation<L extends LeftRotation<?>> {
    // TODO create method that accepts int for number of rotations (i.e. rotL(int n))
    @Contract(value = "-> new")
    @NotNull L rotL();
}
