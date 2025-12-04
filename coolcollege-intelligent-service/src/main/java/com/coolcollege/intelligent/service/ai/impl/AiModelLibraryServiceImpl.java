package com.coolcollege.intelligent.service.ai.impl;

import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.dao.ai.dao.AiModelLibraryDAO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.service.ai.AiModelLibraryService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
@Service
@RequiredArgsConstructor
public class AiModelLibraryServiceImpl implements AiModelLibraryService {
    private final AiModelLibraryDAO aiModelLibraryDAO;

    @Override
    public List<AiModelLibraryDO> getList(Boolean display, String type) {
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        List<AiModelLibraryDO> list = aiModelLibraryDAO.getList(display, type);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        return list;
    }

    @Override
    public AiModelLibraryDO getModelByCode(String code) {
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        AiModelLibraryDO aiModelLibraryDO = aiModelLibraryDAO.getModelByCode(code);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        return aiModelLibraryDO;
    }

    @Override
    public AiModelLibraryDO getPlatformByCode(String code) {
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        AiModelLibraryDO aiModelLibraryDO = aiModelLibraryDAO.getPlatformByCode(code);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        return aiModelLibraryDO;
    }

    @Override
    public Map<String, AiModelLibraryDO> getModelMapByCodes(List<String> codes) {
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        Map<String, AiModelLibraryDO> aiModelMap = aiModelLibraryDAO.getModelMapByCodes(codes);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        return aiModelMap;
    }

    @Override
    public Map<String, String> getModelNameMapByCodes(List<String> codes) {
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        Map<String, String> aiModelMap = aiModelLibraryDAO.getModelNameMapByCodes(codes);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        return aiModelMap;
    }
}
