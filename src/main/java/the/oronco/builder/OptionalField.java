package the.oronco.builder;

import org.jetbrains.annotations.Nullable;

public record OptionalField<T>(@Nullable T value) implements Builder.ConstructableField<T> {
}
