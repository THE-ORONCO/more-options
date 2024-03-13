package the.oronco.tuple;

import org.springframework.data.util.StreamUtils;
import the.oronco.adt.Option;
import the.oronco.adt.Result;
import the.oronco.tuple.Tuple.Size1;
import the.oronco.tuple.Tuple.Size10;
import the.oronco.tuple.Tuple.Size2;
import the.oronco.tuple.Tuple.Size3;
import the.oronco.tuple.Tuple.Size4;
import the.oronco.tuple.Tuple.Size5;
import the.oronco.tuple.Tuple.Size6;
import the.oronco.tuple.Tuple.Size7;
import the.oronco.tuple.Tuple.Size8;
import the.oronco.tuple.Tuple.Size9;
import the.oronco.tuple.TupleError.IndexError;
import the.oronco.tuple.TupleError.IndexError.IndexSmallerZeroError;
import the.oronco.tuple.TupleError.IndexError.IndexTooLargeError;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Théo Roncoletta
 * @since 06.03.24
 **/
public sealed interface Tuple permits MultiValue, Tuple.Size0, Size1, Size10, Size2, Size3, Size4, Size5, Size6, Size7, Size8, Size9 {
    int size();

    boolean contains(Object other);

    default boolean containsAll(Iterable<?> others) {
        return StreamUtils.createStreamFromIterator(others.iterator())
                          .allMatch(this::contains);
    }

    default boolean containsAll(Object... others) {
        return Stream.of(others)
                     .allMatch(this::contains);
    }

    default Result<Option<Object>, IndexError> get(int index) {
        if (index < 0) {
            return Result.err(new IndexSmallerZeroError(index));
        } else {
            return Result.err(new IndexTooLargeError(index, this.size() - 1));
        }
    }

    sealed interface Size0 extends Tuple permits Size1, Empty {
        int SIZE = 0;

        @Override
        default int size() {
            return SIZE;
        }

        @Override
        default boolean contains(Object other) {
            return false;
        }
    }

    sealed interface Size1<T0> extends Tuple, Size0 permits Size2, Unit {
        int SIZE = 1;

        @Override
        default int size() {
            return SIZE;
        }

        T0 _0();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._0(), other)) {
                return true;
            } else {
                return Size0.super.contains(other);
            }
        }

        @Override
        default Result<Option<Object>, IndexError> get(int index) {
            if (index == SIZE - 1) {
                return Result.ok(Option.from(this._0()));
            } else {
                return Size0.super.get(index);
            }
        }
    }

    sealed interface Size2<T0, T1> extends Tuple, Size1<T0> permits Size3, Pair {
        int SIZE = 2;

        @Override
        default int size() {
            return SIZE;
        }

        T1 _1();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._1(), other)) {
                return true;
            } else {
                return Size1.super.contains(other);
            }
        }

        @Override
        default Result<Option<Object>, IndexError> get(int index) {
            if (index == SIZE - 1) {
                return Result.ok(Option.from(this._1()));
            } else {
                return Size1.super.get(index);
            }
        }
    }

    sealed interface Size3<T0, T1, T2> extends Tuple, Size2<T0, T1> permits Size4, Triplet {
        int SIZE = 3;

        @Override
        default int size() {
            return SIZE;
        }

        T2 _2();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._2(), other)) {
                return true;
            } else {
                return Size2.super.contains(other);
            }
        }

        @Override
        default Result<Option<Object>, IndexError> get(int index) {
            if (index == SIZE - 1) {
                return Result.ok(Option.from(Result.ok(this._2())));
            } else {
                return Size2.super.get(index);
            }
        }
    }

    sealed interface Size4<T0, T1, T2, T3> extends Tuple, Size3<T0, T1, T2> permits Size5, Quartet {
        int SIZE = 4;

        @Override
        default int size() {
            return SIZE;
        }

        T3 _3();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._3(), other)) {
                return true;
            } else {
                return Size3.super.contains(other);
            }
        }

        @Override
        default Result<Option<Object>, IndexError> get(int index) {
            if (index == SIZE - 1) {
                return Result.ok(Option.from(Result.ok(this._3())));
            } else {
                return Size3.super.get(index);
            }
        }
    }

    sealed interface Size5<T0, T1, T2, T3, T4> extends Tuple, Size4<T0, T1, T2, T3> permits Size6, Quintet {
        int SIZE = 5;

        @Override
        default int size() {
            return SIZE;
        }

        T4 _4();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._4(), other)) {
                return true;
            } else {
                return Size4.super.contains(other);
            }
        }

        @Override
        default Result<Option<Object>, IndexError> get(int index) {
            if (index == SIZE - 1) {
                return Result.ok(Option.from(Result.ok(this._4())));
            } else {
                return Size4.super.get(index);
            }
        }
    }

    sealed interface Size6<T0, T1, T2, T3, T4, T5> extends Tuple, Size5<T0, T1, T2, T3, T4> permits Size7, Sextet {
        int SIZE = 6;

        @Override
        default int size() {
            return SIZE;
        }

        T5 _5();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._5(), other)) {
                return true;
            } else {
                return Size5.super.contains(other);
            }
        }

        @Override
        default Result<Option<Object>, IndexError> get(int index) {
            if (index == SIZE - 1) {
                return Result.ok(Option.from(Result.ok(this._5())));
            } else {
                return Size5.super.get(index);
            }
        }
    }

    sealed interface Size7<T0, T1, T2, T3, T4, T5, T6> extends Tuple, Size6<T0, T1, T2, T3, T4, T5> permits Size8, Septet {
        public static int SIZE = 7;

        @Override
        default int size() {
            return SIZE;
        }

        T6 _6();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._6(), other)) {
                return true;
            } else {
                return Size6.super.contains(other);
            }
        }

        @Override
        default Result<Option<Object>, IndexError> get(int index) {
            if (index == SIZE - 1) {
                return Result.ok(Option.from(Result.ok(this._6())));
            } else {
                return Size6.super.get(index);
            }
        }
    }

    sealed interface Size8<T0, T1, T2, T3, T4, T5, T6, T7> extends Tuple, Size7<T0, T1, T2, T3, T4, T5, T6> permits Size9, Octet {
        public static int SIZE = 8;

        @Override
        default int size() {
            return SIZE;
        }

        T7 _7();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._7(), other)) {
                return true;
            } else {
                return Size7.super.contains(other);
            }
        }

        @Override
        default Result<Option<Object>, IndexError> get(int index) {
            if (index == SIZE - 1) {
                return Result.ok(Option.from(Result.ok(this._7())));
            } else {
                return Size7.super.get(index);
            }
        }
    }

    sealed interface Size9<T0, T1, T2, T3, T4, T5, T6, T7, T8> extends Tuple, Size8<T0, T1, T2, T3, T4, T5, T6, T7> permits Ennead, Size10 {
        int SIZE = 9;

        @Override
        default int size() {
            return SIZE;
        }

        T8 _8();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._8(), other)) {
                return true;
            } else {
                return Size8.super.contains(other);
            }
        }

        @Override
        default Result<Option<Object>, IndexError> get(int index) {
            if (index == SIZE) {
                return Result.ok(Option.from(Result.ok(this._8())));
            } else {
                return Size8.super.get(index);
            }
        }
    }

    sealed interface Size10<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Tuple, Size9<T0, T1, T2, T3, T4, T5, T6, T7, T8> permits Decade {
        int SIZE = 10;

        @Override
        default int size() {
            return SIZE;
        }

        T9 _9();

        @Override
        default boolean contains(Object other) {
            if (Objects.equals(this._9(), other)) {
                return true;
            } else {
                return Size9.super.contains(other);
            }
        }

        @Override
        default Result<Option<Object>, IndexError> get(int index) {
            if (index == SIZE - 1) {
                return Result.ok(Option.from(Result.ok(this._9())));
            } else {
                return Size9.super.get(index);
            }
        }
    }
}
