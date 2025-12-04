package com.coolcollege.intelligent.service.syslog;

import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * describe: 系统日志操作内容处理上下文
 *
 * @author wangff
 * @date 2025/1/20
 */
@Configuration
public class OpContentContext {

    @Resource
    private IOpContentResolve[] opContentResolves;

    private final Map<OpModuleEnum, IOpContentResolve> map = new HashMap<>();

    @PostConstruct
    public void init() {
        for (IOpContentResolve opContentResolve : opContentResolves) {
            map.put(opContentResolve.getOpModule(), opContentResolve);
        }
    }

    /**
     * 获取实现类
     * @param module 功能模块
     * @return 系统日志操作内容处理实现类
     */
    public IOpContentResolve getContentResolve(OpModuleEnum module) {
        return map.get(module);
    }
}
