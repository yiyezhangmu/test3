package com.coolcollege.intelligent.model.elasticSearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangchenbiao
 * @FileName: GroupKey
 * @Description: 统计key
 * @date 2021-10-26 13:37
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface GroupKey {
}
