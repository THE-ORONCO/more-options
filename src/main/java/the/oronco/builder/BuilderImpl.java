package the.oronco.builder;

public interface BuilderImpl<T, B extends BuilderImpl<T, B>> {
    T build();

    default B self() {
        return (B) this;
    }
}
