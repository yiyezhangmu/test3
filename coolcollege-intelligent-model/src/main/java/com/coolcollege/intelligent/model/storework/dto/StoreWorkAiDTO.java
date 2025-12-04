package com.coolcollege.intelligent.model.storework.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

/**
 * <p>
 * 店务AI使用情况DTO
 * </p>
 *
 * @author wangff
 * @since 2025/5/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreWorkAiDTO {
    /**
     * 是否使用ai
     */
    private Boolean useAi = false;

    /**
     * 是否所有门店使用AI
     */
    private Boolean allStore = true;

    /**
     * 是否执行AI分析
     */
    private Set<String> aiStoreSet = Collections.emptySet();

    public static StoreWorkAiDTO notUseAi() {
        return StoreWorkAiDTO.builder().useAi(false).build();
    }

    public static StoreWorkAiDTO allStore() {
        return StoreWorkAiDTO.builder().useAi(true).allStore(true).build();
    }

    public static StoreWorkAiDTO build(Set<String> storeIds) {
        return StoreWorkAiDTO.builder().useAi(true).allStore(false).aiStoreSet(storeIds).build();
    }
}
