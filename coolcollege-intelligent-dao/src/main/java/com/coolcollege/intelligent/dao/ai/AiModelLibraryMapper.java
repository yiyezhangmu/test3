package com.coolcollege.intelligent.dao.ai;

import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-08-01 09:28
 */
public interface AiModelLibraryMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-08-01 09:28
     */
    int insertSelective(AiModelLibraryDO record);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-08-01 09:28
     */
    int updateByPrimaryKeySelective(AiModelLibraryDO record);

    /**
     * 列表查询
     * @param display 是否展示
     * @param type 类型，platform平台/model模型
     * @return 实体列表
     */
    List<AiModelLibraryDO> getList(@Param("display") Boolean display, @Param("type") String type);

    /**
     * 根据code查询模型
     */
    AiModelLibraryDO getByCode(@Param("code") String code, @Param("type") String type);

    /**
     * 根据code查询模型
     */
    List<AiModelLibraryDO> getModelMapByCodes(@Param("codes") List<String> codes);
}