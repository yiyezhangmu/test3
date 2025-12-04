package com.coolcollege.intelligent.service.ai;

import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * AI算法模型库
 * </p>
 *
 * @author wangff
 * @since 2025/8/1
 */
public interface AiModelLibraryService {

    /**
     * 列表查询
     * @param display 是否展示
     * @param type 类型，platform平台/model模型
     * @return 实体列表
     */
    List<AiModelLibraryDO> getList(Boolean display, String type);

    /**
     * 根据模型code查询
     */
    AiModelLibraryDO getModelByCode(String code);

    /**
     * 根据平台code查询
     */
    AiModelLibraryDO getPlatformByCode(String code);

    /**
     * 根据模型code查询模型映射
     */
    Map<String, AiModelLibraryDO> getModelMapByCodes(List<String> codes);

    /**
     * 根据模型code查询模型名称映射
     */
    Map<String, String> getModelNameMapByCodes(List<String> codes);
}
