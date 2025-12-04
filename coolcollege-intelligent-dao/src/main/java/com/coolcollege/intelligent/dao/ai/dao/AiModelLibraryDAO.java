package com.coolcollege.intelligent.dao.ai.dao;

import com.coolcollege.intelligent.dao.ai.AiModelLibraryMapper;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
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
@RequiredArgsConstructor
@Repository
public class AiModelLibraryDAO {
    private final AiModelLibraryMapper aiModelLibraryMapper;

    /**
     * 列表查询
     * @param display 是否展示
     * @param type 类型，platform平台/model模型
     * @return 实体列表
     */
    public List<AiModelLibraryDO> getList(Boolean display, String type) {
        return aiModelLibraryMapper.getList(display, type);
    }

    /**
     * 根据code查询模型
     */
    public AiModelLibraryDO getModelByCode(String code) {
        return aiModelLibraryMapper.getByCode(code, "model");
    }

    /**
     * 根据code查询平台
     */
    public AiModelLibraryDO getPlatformByCode(String code) {
        return aiModelLibraryMapper.getByCode(code, "platform");
    }

    /**
     * 根据code查询模型映射
     */
    public Map<String, AiModelLibraryDO> getModelMapByCodes(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptyMap();
        }
        List<AiModelLibraryDO> models = aiModelLibraryMapper.getModelMapByCodes(codes);
        Map<String, AiModelLibraryDO> map = new HashMap<>();
        for (AiModelLibraryDO model : models) {
            map.put(model.getCode(), model);
            map.put(model.getAliasCode(), model);
        }
        return map;
    }

    /**
     * 根据code查询模型映射
     */
    public Map<String, String> getModelNameMapByCodes(List<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptyMap();
        }
        List<AiModelLibraryDO> models = aiModelLibraryMapper.getModelMapByCodes(codes);
        Map<String, String> map = new HashMap<>();
        for (AiModelLibraryDO model : models) {
            map.put(model.getCode(), model.getName());
            map.put(model.getAliasCode(), model.getName());
        }
        return map;
    }
}
