package com.coolcollege.intelligent.service.syslog;

import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.model.syslog.SysLogDO;

import java.util.Map;

/**
 * describe: 系统日志操作内容处理
 *
 * @author wangff
 * @date 2025/1/20
 */
public interface IOpContentResolve {

    /**
     * 获取功能模块
     * @return 功能模块枚举类
     */
    OpModuleEnum getOpModule();

    /**
     * 内容处理
     * @param enterpriseId 企业id
     * @param typeEnum 操作类型
     * @param sysLogDO 系统日志实体对象
     */
    String resolve(String enterpriseId, OpTypeEnum typeEnum, SysLogDO sysLogDO);

    /**
     * 原方法调用前预处理
     * <P/> 日志AOP在Before阶段处理，处理结果会存在系统日志的extendInfo中
     *
     * @param enterpriseId 企业id
     * @param reqParams 原方法参数列表
     * @param typeEnum 操作类型
     * @return 处理结果
     */
    String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum);
}
