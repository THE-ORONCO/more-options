package the.oronco.builder;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public interface Builder<T, B extends Builder<T, B>> extends BuilderImpl<T, B> {

    interface Value0<T, B extends Value0<T, B, V0>, V0> extends Builder<T, B> {
        V0 _f0();
    }

    interface Value1<T, B extends Value1<T, B, V0, F0, V1, F1>, V0, F0 extends Field<V0>, V1, F1 extends Field<V1>> extends Builder<T, B> {

    }

    interface RequiredValue0<T, B extends RequiredValue0<T, B, V0, RequiredField<V0>>, V0, F0 extends RequiredField<V0>> extends Value0<T, B, V0> {
        RequiredValue0<T, B, V0, RequiredField.Yes<V0>> _f0(@NotNull V0 fieldValue0);
    }

    interface RequiredValue1<T, B extends RequiredValue1<T, B, V0, F0, V1, F1>, V0, F0 extends RequiredField<V0>, V1, F1 extends RequiredField<V1>>
            extends Value1<T, B, V0, F0, V1, F1> {
    }

    interface DefaultValue0<T, B extends DefaultValue0<T, B, V0, F0>, V0, F0 extends DefaultField<V0>> extends Value0<T, B, V0> {
        DefaultValue0<T, B, V0, F0> _f0(V0 fieldValue0);

    }

    interface Field<F> {

    }

    interface ConstructableField<F> extends Field<F> {
        F value();
    }

    static <T, B extends Value0<T, B, V0>, V0, F0 extends ConstructableField<V0>> T construct(B builder) {
        return builder.build();
    }

    static <T, B extends Value1<T, B, V0, F0, V1, F1>, V0, F0 extends ConstructableField<V0>, V1, F1 extends ConstructableField<V1>> T construct(B builder) {
        return builder.build();
    }

    @AllArgsConstructor
    @Data
    static class Testy {

        private String value;
        private Integer otherValue;

        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class TestyBuilder<F0 extends RequiredField<String>, F1 extends RequiredField<Integer>>
                implements RequiredValue1<Testy, TestyBuilder<F0, F1>, String, F0, Integer, F1> {

            private F0 value;
            private F1 otherValue;

            @Override
            public Testy build() {
                return new Testy(((RequiredField.Yes<String>) value).value(), ((RequiredField.Yes<Integer>) otherValue).value());
            }


            public TestyBuilder<RequiredField.Yes<String>, F1> value(@NotNull @NonNull String fieldValue0) {
                return new TestyBuilder<>(new RequiredField.Yes<>(fieldValue0), otherValue);
            }

            public TestyBuilder<F0, RequiredField.Yes<Integer>> otherValue(@NotNull @NonNull Integer fieldValue1) {
                return new TestyBuilder<>(value, new RequiredField.Yes<>(fieldValue1));
            }

        }

        public static TestyBuilder<RequiredField.No<String>, RequiredField.No<Integer>> builder() {
            return new TestyBuilder<>(new RequiredField.No<>(), new RequiredField.No<>());
        }
    }

    public static void main(String[] args) {
        Testy a = construct(Testy.builder()
                                 .value("a")
                                 .otherValue(1));
        Testy b = construct(Testy.builder()
                                 .otherValue(1)
                                 .value("1"));
        System.out.println(a);
        System.out.println(b);
    }

}
