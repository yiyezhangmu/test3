package com.coolcollege.intelligent.util.transation;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

/**
 * describe: 编程式事务
 *
 * @author zhouyiping
 * @date 2021/03/25
 */
@Component
public class Transactor{


        @Transactional( rollbackFor = java.lang.Exception.class)
        public <R> R callBack(Supplier<R> f) {
            return f.get(); // 有返回值的代码块
        }

        @Transactional(rollbackFor = java.lang.Exception.class)
        public void run(Runnable f) {
            f.run(); // 无返回值的代码块
        }



}
