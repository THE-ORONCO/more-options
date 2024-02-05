package the.oronco.iter;

import the.oronco.adt.Option;

/**
 * @author the_oronco@posteo.net
 * @since 05/02/2024
 */
class FlattenCompact<I, U> {
        private final Iter<I> iter;
        private final Option<U> frontiter;
        private final Option<U> backiter;

        protected FlattenCompact(Iter<I> iter){
            this.iter = iter;
            this.backiter = Option.none();
            this.frontiter = Option.none();
        }
}
