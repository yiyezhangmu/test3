package com.coolcollege.intelligent.common.constant;

public class TwoResultTuple<A, B> {
    public final A first;
    public final B second;

    public TwoResultTuple(A a, B b) {
        this.first = a;
        this.second = b;
    }
}
