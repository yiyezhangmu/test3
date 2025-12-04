package com.coolcollege.intelligent.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 * AI配置巡检模块枚举类
 * </p>
 *
 * @author wangff
 * @since 2025/6/9
 */
@Getter
@RequiredArgsConstructor
public enum AIBusinessModuleEnum {

    STORE_WORK("storeWork", "店务"),
    PATROL_STORE_OFFLINE("patrolStoreOffline", "线下巡店"),

    ;

    /**
     * 模块
     */
    private final String module;

    /**
     * 描述
     */
    private final String msg;
}
