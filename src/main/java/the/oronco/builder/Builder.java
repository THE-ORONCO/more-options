package the.oronco.builder;


public interface Builder<T, B extends Builder<T, B>> {

    abstract class Buildable<T> {
        protected abstract T build();
    }

    abstract class Value0<T, B extends Value0<T, B, V0, F0>, V0, F0 extends Field<V0>> extends Buildable<T> implements Builder<T, B> {
    }

    abstract class Value1<T, B extends Value1<T, B, V0, F0, V1, F1>, V0, F0 extends Field<V0>, V1, F1 extends Field<V1>> extends Buildable<T>
            implements Builder<T, B> {
    }

    abstract class RequiredValue0<T, B extends RequiredValue0<T, B, V0, F0>, V0, F0 extends RequiredField<V0>> extends Value0<T, B, V0, F0> {
    }

    abstract class RequiredValue1<T, B extends RequiredValue1<T, B, V0, F0, V1, F1>, V0, F0 extends RequiredField<V0>, V1,
            F1 extends RequiredField<V1>>
            extends Value1<T, B, V0, F0, V1, F1> {
    }

    interface Field<F> {

    }

    interface ConstructableField<F> extends Field<F> {
        F value();
    }

    static <T, B extends Value0<T, B, V0, F0>, V0, F0 extends ConstructableField<V0>> T buildWith(B builder) {
        return builder.build();
    }

    static <T, B extends Value1<T, B, V0, F0, V1, F1>, V0, F0 extends ConstructableField<V0>, V1, F1 extends ConstructableField<V1>> T buildWith(B builder) {
        return builder.build();
    }

}
