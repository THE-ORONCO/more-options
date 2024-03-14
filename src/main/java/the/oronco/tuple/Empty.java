package the.oronco.tuple;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Théo Roncoletta
 * @since 13.03.24
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class Empty implements MultiValue<Empty, Empty, Empty>, Tuple.Size0 {

    @MagicConstant
    public static final Empty EMPTY = new Empty();

    public static @NotNull Empty of() {
        return EMPTY;
    }

    @Override
    public @NotNull Empty rotL() {
        return EMPTY;
    }

    @Override
    public @Unmodifiable Collection<Object> j() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull Empty rotR() {
        return EMPTY;
    }

    @Override
    public String toString() {
        return "()";
    }
}
