package the.oronco.builder;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

sealed interface RequiredField<T> extends Builder.Field<T> {
    record No<T>() implements RequiredField<T> {
    }

    record Yes<T>(@NotNull @NonNull T value) implements RequiredField<T>, Builder.ConstructableField<T> {
    }
}
