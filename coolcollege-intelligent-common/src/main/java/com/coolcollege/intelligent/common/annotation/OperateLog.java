package com.coolcollege.intelligent.common.annotation;

/**
 * 自定义操作日志注解
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2021/1/7 14:14
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {
    /**
     * 操作模块
     * @return
     */
    String operateModule() default "";
    /**
     * 操作类型
     * @return
     */
    String operateType() default "";
    /**
     * 操作说明
     * @return
     */
    String operateDesc() default "";
}