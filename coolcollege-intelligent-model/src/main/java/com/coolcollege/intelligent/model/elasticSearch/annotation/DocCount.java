package com.coolcollege.intelligent.model.elasticSearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangchenbiao
 * @FileName: DocCount
 * @Description: 文档数量
 * @date 2021-10-26 16:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface DocCount {
}
