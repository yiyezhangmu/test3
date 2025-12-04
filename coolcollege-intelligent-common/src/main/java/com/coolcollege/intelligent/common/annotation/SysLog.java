package com.coolcollege.intelligent.common.annotation;

import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;

import java.lang.annotation.*;

/**
 * describe: 系统日志
 *
 * @author wangff
 * @date 2025/1/20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    /**
     * 功能
     */
    String func();
    /**
     * 子功能
     */
    String subFunc() default "";
    /**
     * 模块
     */
    OpModuleEnum opModule();
    /**
     * 操作类型
     */
    OpTypeEnum opType();
    /**
     * 是否处理操作内容
     */
    boolean resolve() default true;
    /**
     * 是否有前置处理
     */
    boolean preprocess() default false;

    /**
     * 导入导出等特殊处理的接口，使用这个参数
     */
    String menus() default "";
}
