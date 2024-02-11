package the.oronco.adt;

/**
 * @author the_oronco@posteo.net
 * @since 11/02/2024
 */
public interface Try<O//Output
        , R//Residual
> {
    ControlFlow<R, O> branch();
}
