package com.coolcollege.intelligent.dao.homeTemplate;

import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 14:32
 * @Version 1.0
 */
@Mapper
public interface HomeTemplateMapper {

    /**
     * 批量新增
     * @param enterpriseId
     * @param list
     */
    void batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<HomeTemplateDO> list);

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
    Boolean updateById(@Param("enterpriseId") String enterpriseId,@Param("entity") HomeTemplateDO entity);

    /**
     * 根据主键id查询数据
     * @param enterpriseId
     * @param id
     * @return
     */
    HomeTemplateDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Integer id);


    /**
     * 批量新增
     * @param enterpriseId
     * @param homeTemplateDO
     */
    void insert(@Param("enterpriseId") String enterpriseId, @Param("entity") HomeTemplateDO homeTemplateDO);


    /**
     * 插入模板
     * @param enterpriseId
     * @param homeTemplateDO
     */
    void insertIdTemplate(@Param("enterpriseId") String enterpriseId, @Param("entity") HomeTemplateDO homeTemplateDO);



    /**
     * 查询所有的模板 不包括删除状态的数据
     * @param enterpriseId
     * @param templateName
     * @return
     */
    List<HomeTemplateDO> selectAllData(@Param("enterpriseId") String enterpriseId, @Param("templateName") String templateName);

    /**
     * 根据id集合查询数据
     * @param enterpriseId
     * @param ids
     * @return
     */
    List<HomeTemplateDO> selectByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Integer> ids);

}
