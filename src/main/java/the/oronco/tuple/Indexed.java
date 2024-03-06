package the.oronco.tuple;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
public sealed interface Indexed {
    sealed interface Value0<T0> extends Indexed permits Value1, Unit {
        T0 _0();
    }

    sealed interface Value1<T0, T1> extends Indexed, Value0<T0> permits Value2, Pair {
        T1 _1();
    }

    sealed interface Value2<T0, T1, T2> extends Indexed, Value1<T0, T1> permits Value3, Triplet {
        T2 _2();
    }

    sealed interface Value3<T0, T1, T2, T3> extends Indexed, Value2<T0, T1, T2> permits Value4, Quartet {
        T3 _3();
    }

    sealed interface Value4<T0, T1, T2, T3, T4> extends Indexed, Value3<T0, T1, T2, T3> permits Value5, Quintet {
        T4 _4();
    }

    sealed interface Value5<T0, T1, T2, T3, T4, T5> extends Indexed, Value4<T0, T1, T2, T3, T4> permits Value6, Sextet {
        T5 _5();
    }

    sealed interface Value6<T0, T1, T2, T3, T4, T5, T6> extends Indexed, Value5<T0, T1, T2, T3, T4, T5> permits Value7, Septet {
        T6 _6();
    }

    sealed interface Value7<T0, T1, T2, T3, T4, T5, T6, T7> extends Indexed, Value6<T0, T1, T2, T3, T4, T5, T6>
            permits Value8, Octet {
        T7 _7();
    }

    sealed interface Value8<T0, T1, T2, T3, T4, T5, T6, T7, T8> extends Indexed, Value7<T0, T1, T2, T3, T4, T5, T6, T7>
            permits Ennead, Value9 {
        T8 _8();
    }

    sealed interface Value9<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Indexed, Value8<T0, T1, T2, T3, T4, T5, T6, T7, T8>
            permits Decade {
        T9 _9();
    }
}
