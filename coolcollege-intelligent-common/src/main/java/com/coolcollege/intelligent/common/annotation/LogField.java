package com.coolcollege.intelligent.common.annotation;

import java.lang.annotation.*;

/**
 * @Author: huhu
 * @Date: 2025/1/20 14:54
 * @Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
@Documented
public @interface LogField {
    String name() default "";

}
