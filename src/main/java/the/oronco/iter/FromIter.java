package the.oronco.iter;

public interface FromIter<A, I extends Iter<A>, T extends IntoIter<A,I>, S extends FromIter<A,I,T,S>> {
    S fromIter(I iter);

    default S fromIter(T intoIter) {
        return this.fromIter(intoIter.intoIter());
    }
}
