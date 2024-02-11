package the.oronco.iter;

public interface IntoIter<T, I extends Iter<T>> {
    I intoIter();
}
