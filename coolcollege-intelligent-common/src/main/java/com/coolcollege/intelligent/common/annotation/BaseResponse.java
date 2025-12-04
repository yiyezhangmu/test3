package com.coolcollege.intelligent.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 统一响应注解   (该注解已经过期请，谨慎修改原有的BaseResponse，新的Controller请使用使用ErrorHelper)
 * @author Aaron
 * @date 2019/12/20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Deprecated
public @interface BaseResponse {
}
