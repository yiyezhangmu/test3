package com.coolcollege.intelligent.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: ErrorHelper
 * @Author: mao
 * @CreateDate: 2021/5/23 18:24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ErrorHelper {
}
