package com.coolcollege.intelligent.dao.brand;

import com.coolcollege.intelligent.model.brand.EnterpriseBrandDO;
import com.coolcollege.intelligent.model.brand.request.EnterpriseBrandQueryRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-03-06 10:28
 */
public interface EnterpriseBrandMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-03-06 10:28
     */
    int insertSelective(@Param("record") EnterpriseBrandDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-03-06 10:28
     */
    int updateByPrimaryKeySelective(@Param("record") EnterpriseBrandDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 品牌名称是否存在
     * @param enterpriseId 企业id
     * @param name 品牌名称
     * @return boolean 是否存在
     */
    Boolean existsNameExcludeId(@Param("enterpriseId") String enterpriseId, @Param("id") Long id, @Param("name") String name);

    /**
     * 批量删除
     * @param enterpriseId 企业id
     * @param ids id列表
     * @return int
     */
    int deleteBatch(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 根据id查询
     * @param enterpriseId 企业id
     * @param id id
     * @return 实体
     */
    EnterpriseBrandDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    List<EnterpriseBrandDO> selectByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 列表查询
     * @param enterpriseId 企业id
     * @param request 品牌查询request
     * @return 实体列表
     */
    List<EnterpriseBrandDO> getList(@Param("enterpriseId") String enterpriseId, @Param("request")EnterpriseBrandQueryRequest request);

    /**
     * 根据id获取品牌名称
     * @param enterpriseId 企业id
     * @param ids id列表
     * @return 实体列表
     */
    List<EnterpriseBrandDO> getNameByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 品牌code是否存在
     * @param enterpriseId 企业id
     * @param code 品牌code
     * @return 是否存在
     */
    boolean existsByCode(@Param("enterpriseId") String enterpriseId, @Param("code") String code);
}