package com.coolcollege.intelligent.common.constant;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/22
 */
@Data
public class ThreeResultTuple<A,B,C> {
    private  A first;
    private  B second;
    private  C three;

    public ThreeResultTuple(A a, B b,C c) {
        this.first = a;
        this.second = b;
        this.three=c;
    }
}
