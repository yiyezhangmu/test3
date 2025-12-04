package com.coolcollege.intelligent.service.ai;

import com.coolcollege.intelligent.model.enums.AIPlatformEnum;
import com.coolcollege.intelligent.service.ai.impl.BailianAIOpenServiceImpl;
import com.coolstore.base.utils.CommonContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * AI处理工厂
 * </p>
 *
 * @author wangff
 * @since 2025/6/5
 */
@Service
@DependsOn("commonContextUtil")
public class AIOpenFactory {
    private final Map<String, AIOpenService> map = new ConcurrentHashMap<>();

    @Autowired
    public AIOpenFactory(Map<String, AIOpenService> map) {
        this.map.putAll(map);
    }

    public AIOpenService getAIResolve(AIPlatformEnum aiPlatformEnum) {
        return map.getOrDefault(aiPlatformEnum.getBeanName(), CommonContextUtil.getBean(BailianAIOpenServiceImpl.class));
    }
}
