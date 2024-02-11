package the.oronco.adt;

/**
 * @author the_oronco@posteo.net
 * @since 11/02/2024
 */
public interface Self<S extends Self<S>> {
     S self();
}
