package the.oronco;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import the.oronco.builder.Builder;
import the.oronco.builder.RequiredField;


@AllArgsConstructor
@Data
public class Testy {

    private String value;
    private Integer otherValue;

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TestyBuilder<F0 extends RequiredField<String>, F1 extends RequiredField<Integer>>
            extends Builder.RequiredValue1<Testy, TestyBuilder<F0, F1>, String, F0, Integer, F1> {

        private F0 value;
        private F1 otherValue;

        public TestyBuilder<RequiredField.Present<String>, F1> value(@NotNull @NonNull String fieldValue0) {
            return new TestyBuilder<>(new RequiredField.Present<>(fieldValue0), otherValue);
        }

        public TestyBuilder<F0, RequiredField.Present<Integer>> otherValue(@NotNull @NonNull Integer fieldValue1) {
            return new TestyBuilder<>(value, new RequiredField.Present<>(fieldValue1));
        }


        @Override
        protected Testy build() {
            return new Testy(value.value(), otherValue.value());
        }
    }

    public static TestyBuilder<RequiredField.Missing<String>, RequiredField.Missing<Integer>> builder() {
        return new TestyBuilder<>(new RequiredField.Missing<>(), new RequiredField.Missing<>());
    }

}
