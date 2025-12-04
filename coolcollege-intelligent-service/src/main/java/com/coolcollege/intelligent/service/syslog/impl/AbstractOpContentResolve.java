package com.coolcollege.intelligent.service.syslog.impl;

import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.service.syslog.IOpContentResolve;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.*;

/**
 * describe: 系统日志操作内容策略
 *
 * @author wangff
 * @date 2025/1/20
 */
@Slf4j
public abstract class AbstractOpContentResolve implements IOpContentResolve {

    protected Map<OpTypeEnum, BiFunction<String, SysLogDO, String>> funcMap = new HashMap<>();

    @PostConstruct
    protected void init() {
        funcMap.put(INSERT, this::insert);
        funcMap.put(EDIT, this::edit);
        funcMap.put(DELETE, this::delete);
        funcMap.put(IMPORT, this::importFile);
        funcMap.put(EXPORT, this::exportFile);
        funcMap.put(REMIND, this::remind);
        funcMap.put(STOP, this::stop);
        funcMap.put(REALLOCATE, this::reallocate);
        funcMap.put(ARCHIVE, this::archive);
    }

    @Override
    public String resolve(String enterpriseId, OpTypeEnum typeEnum, SysLogDO sysLogDO) {
        BiFunction<String, SysLogDO, String> func = funcMap.get(typeEnum);
        if (Objects.nonNull(func)) {
            try {
                return func.apply(enterpriseId, sysLogDO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        return "";
    }

    /**
     * 新增
     */
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        return "";
    }

    /**
     * 修改
     */
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        return "";
    }

    /**
     * 删除
     */
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        return "";
    }

    /**
     * 批量删除
     */
    protected String batchDelete(String enterpriseId, SysLogDO sysLogDO) {
        return "";
    }

    /**
     * 催办
     */
    protected String remind(String enterpriseId, SysLogDO sysLogDO) {
        return "";
    }
    
    /**
     * 停止
     */
    protected String stop(String enterpriseId, SysLogDO sysLogDO) {
        return "";
    }

    /**
     * 重新分配
     */
    protected String reallocate(String enterpriseId, SysLogDO sysLogDO) {
        return "";
    }

    /**
     * 归档
     */
    protected String archive(String enterpriseId, SysLogDO sysLogDO) {
        return "";
    }

    /**
     * 导入
     */
    protected String importFile(String enterpriseId, SysLogDO sysLogDO) {
        return "";
    }

    /**
     * 导出
     */
    protected String exportFile(String enterpriseId, SysLogDO sysLogDO) {
        return "";
    }
}
