package the.oronco.iter;

public interface FromIter<T, F extends FromIter<T, F>> {
    F fromIter(Iter<T> iter);
   default F fromIter(IntoIter<T> intoIter){
        return this.fromIter(intoIter.intoIter());
    }
}
