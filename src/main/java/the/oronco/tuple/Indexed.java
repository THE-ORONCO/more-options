package the.oronco.tuple;

import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.data.util.StreamUtils;
import the.oronco.adt.Result;
import the.oronco.tuple.Indexed.Value0;
import the.oronco.tuple.Indexed.Value1;
import the.oronco.tuple.Indexed.Value2;
import the.oronco.tuple.Indexed.Value3;
import the.oronco.tuple.Indexed.Value4;
import the.oronco.tuple.Indexed.Value5;
import the.oronco.tuple.Indexed.Value6;
import the.oronco.tuple.Indexed.Value7;
import the.oronco.tuple.Indexed.Value8;
import the.oronco.tuple.Indexed.Value9;
import the.oronco.tuple.TupleError.IndexError;
import the.oronco.tuple.TupleError.IndexError.IndexSmallerZeroError;
import the.oronco.tuple.TupleError.IndexError.IndexTooLargeError;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
public sealed interface Indexed permits Value0, Value1, Value2, Value3, Value4, Value5, Value6, Value7, Value8, Value9, Tuple {
    int size();

    default boolean contains(Object other) {
        return false;
    }

    default boolean containsAll(Iterable<?> others) {
        return StreamUtils.createStreamFromIterator(others.iterator())
                          .allMatch(this::contains);
    }

    default boolean containsAll(Object... others) {
        return Stream.of(others)
                     .allMatch(this::contains);
    }

    default Result<Object, IndexError> get(int index) {
        if (index < 0) {
            return Result.err(new IndexSmallerZeroError(index));
        } else {
            return Result.err(new IndexTooLargeError(index, this.size() - 1));
        }
    }

    sealed interface Value0<T0> extends Indexed permits Value1, Unit {
        T0 _0();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._0(), other)) {
                return true;
            } else {
                return Indexed.super.contains(other);
            }
        }

        @Override
        default Result<Object, IndexError> get(int index) {
            if (index == 0) {
                return Result.ok(this._0());
            } else {
                return Indexed.super.get(index);
            }
        }
    }

    sealed interface Value1<T0, T1> extends Indexed, Value0<T0> permits Value2, Pair {
        T1 _1();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._1(), other)) {
                return true;
            } else {
                return Value0.super.contains(other);
            }
        }

        @Override
        default Result<Object, IndexError> get(int index) {
            if (index == 1) {
                return Result.ok(this._1());
            } else {
                return Value0.super.get(index);
            }
        }
    }

    sealed interface Value2<T0, T1, T2> extends Indexed, Value1<T0, T1> permits Value3, Triplet {
        T2 _2();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._2(), other)) {
                return true;
            } else {
                return Value1.super.contains(other);
            }
        }

        @Override
        default Result<Object, IndexError> get(int index) {
            if (index == 2) {
                return Result.ok(this._2());
            } else {
                return Value1.super.get(index);
            }
        }
    }

    sealed interface Value3<T0, T1, T2, T3> extends Indexed, Value2<T0, T1, T2> permits Value4, Quartet {
        T3 _3();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._3(), other)) {
                return true;
            } else {
                return Value2.super.contains(other);
            }
        }

        @Override
        default Result<Object, IndexError> get(int index) {
            if (index == 3) {
                return Result.ok(this._3());
            } else {
                return Value2.super.get(index);
            }
        }
    }

    sealed interface Value4<T0, T1, T2, T3, T4> extends Indexed, Value3<T0, T1, T2, T3> permits Value5, Quintet {
        T4 _4();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._4(), other)) {
                return true;
            } else {
                return Value3.super.contains(other);
            }
        }

        @Override
        default Result<Object, IndexError> get(int index) {
            if (index == 4) {
                return Result.ok(this._4());
            } else {
                return Value3.super.get(index);
            }
        }
    }

    sealed interface Value5<T0, T1, T2, T3, T4, T5> extends Indexed, Value4<T0, T1, T2, T3, T4> permits Value6, Sextet {
        T5 _5();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._5(), other)) {
                return true;
            } else {
                return Value4.super.contains(other);
            }
        }

        @Override
        default Result<Object, IndexError> get(int index) {
            if (index == 5) {
                return Result.ok(this._5());
            } else {
                return Value4.super.get(index);
            }
        }
    }

    sealed interface Value6<T0, T1, T2, T3, T4, T5, T6> extends Indexed, Value5<T0, T1, T2, T3, T4, T5> permits Value7, Septet {
        T6 _6();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._6(), other)) {
                return true;
            } else {
                return Value5.super.contains(other);
            }
        }

        @Override
        default Result<Object, IndexError> get(int index) {
            if (index == 6) {
                return Result.ok(this._6());
            } else {
                return Value5.super.get(index);
            }
        }
    }

    sealed interface Value7<T0, T1, T2, T3, T4, T5, T6, T7> extends Indexed, Value6<T0, T1, T2, T3, T4, T5, T6>
            permits Value8, Octet {
        T7 _7();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._7(), other)) {
                return true;
            } else {
                return Value6.super.contains(other);
            }
        }

        @Override
        default Result<Object, IndexError> get(int index) {
            if (index == 7) {
                return Result.ok(this._7());
            } else {
                return Value6.super.get(index);
            }
        }
    }

    sealed interface Value8<T0, T1, T2, T3, T4, T5, T6, T7, T8> extends Indexed, Value7<T0, T1, T2, T3, T4, T5, T6, T7>
            permits Ennead, Value9 {
        T8 _8();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._8(), other)) {
                return true;
            } else {
                return Value7.super.contains(other);
            }
        }

        @Override
        default Result<Object, IndexError> get(int index) {
            if (index == 8) {
                return Result.ok(this._8());
            } else {
                return Value7.super.get(index);
            }
        }
    }

    sealed interface Value9<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Indexed, Value8<T0, T1, T2, T3, T4, T5, T6, T7, T8>
            permits Decade {
        T9 _9();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._9(), other)) {
                return true;
            } else {
                return Value8.super.contains(other);
            }
        }

        @Override
        default Result<Object, IndexError> get(int index) {
            if (index == 9) {
                return Result.ok(this._9());
            } else {
                return Value8.super.get(index);
            }
        }
    }
}
