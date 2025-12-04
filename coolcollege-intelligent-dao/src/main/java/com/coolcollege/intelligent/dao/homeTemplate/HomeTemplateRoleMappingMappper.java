package com.coolcollege.intelligent.dao.homeTemplate;

import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateRoleMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 14:32
 * @Version 1.0
 */
@Mapper
public interface HomeTemplateRoleMappingMappper {
    /**
     * 批量新增
     * @param enterpriseId
     * @param list
     */
    void batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<HomeTemplateRoleMappingDO> list);

    /**
     * 根据ids批量删除
     * @param enterpriseId
     * @param ids
     */
    void deletedByIds(@Param("enterpriseId") String enterpriseId,@Param("ids") List<Integer> ids);

    /**
     * 编辑数据
     * @param entity
     * @return
     */
    Boolean updateById(@Param("enterpriseId") String enterpriseId,@Param("entity") HomeTemplateRoleMappingDO entity);

    /**
     * 根据主键id查询数据
     * @param id
     * @return
     */
    HomeTemplateRoleMappingDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Integer id);


    /**
     * 删除角色对应的模板映射
     * @param enterpriseId
     * @param ids
     */
    void deletedByRoleIds(@Param("enterpriseId") String enterpriseId,@Param("ids") List<Long> ids);

    /**
     * 删除模板的所有角色
     * @param enterpriseId
     * @param templateId
     */
    void deletedByTemplateId(@Param("enterpriseId") String enterpriseId,@Param("templateId") Integer templateId);


    /**
     * 根据主键id查询数据
     * @param ids
     * @return
     */
    List<HomeTemplateRoleMappingDO> selectByHomeTemplateId(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Integer> ids);

    /**
     * 根据主键id查询数据
     * @param ids
     * @return
     */
    List<HomeTemplateRoleMappingDO> selectByRoleIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids);
}
