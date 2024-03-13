package the.oronco.adt;

/**
 * @param <O> output
 * @param <R> residual
 * @author the_oronco@posteo.net
 * @since 11/02/2024
 */
public interface Try<O, R> {
    ControlFlow<R, O> branch();
}
