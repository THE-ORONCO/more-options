package the.oronco.tuple;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
public sealed interface Indexed {
    sealed interface Value0<T> extends Indexed permits Decade, Ennead, Octet, Pair, Quartet, Quintet, Septet, Sextet, Triplet, Unit {
        T _0();
    }

    sealed interface Value1<T> extends Indexed permits Decade, Ennead, Octet, Pair, Quartet, Quintet, Septet, Sextet, Triplet {
        T _1();
    }

    sealed interface Value2<T> extends Indexed permits Decade, Ennead, Octet, Quartet, Quintet, Septet, Sextet, Triplet {
        T _2();
    }

    sealed interface Value3<T> extends Indexed permits Decade, Ennead, Octet, Quartet, Quintet, Septet, Sextet {
        T _3();
    }

    sealed interface Value4<T> extends Indexed permits Decade, Ennead, Octet, Quintet, Septet, Sextet {
        T _4();
    }

    sealed interface Value5<T> extends Indexed permits Decade, Ennead, Octet, Septet, Sextet {
        T _5();
    }

    sealed interface Value6<T> extends Indexed permits Decade, Ennead, Octet, Septet {
        T _6();
    }

    sealed interface Value7<T> extends Indexed permits Decade, Ennead, Octet {
        T _7();
    }

    sealed interface Value8<T> extends Indexed permits Decade, Ennead {
        T _8();
    }

    sealed interface Value9<T> extends Indexed permits Decade {
        T _9();
    }
}
