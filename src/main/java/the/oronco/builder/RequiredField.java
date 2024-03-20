package the.oronco.builder;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public sealed interface RequiredField<T> extends Builder.Field<T> {
    T value() throws IllegalStateException;

    record Missing<T>() implements RequiredField<T> {
        @Override
        public T value() throws IllegalStateException {
            throw new IllegalStateException("Field was required but not present at access time!");
        }
    }

    record Present<T>(@NotNull @NonNull T value) implements RequiredField<T>, Builder.ConstructableField<T> {
    }
}
