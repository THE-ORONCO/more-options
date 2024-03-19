package the.oronco.builder;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public record DefaultField<T>(@NotNull @NonNull T value) implements Builder.ConstructableField<T> {
}
