package com.coolcollege.intelligent.mapper.homeTemplate;

import com.coolcollege.intelligent.dao.homeTemplate.HomeTemplateMapper;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 14:56
 * @Version 1.0
 */
@Service
public class HomeTemplateDAO {
    @Autowired
    HomeTemplateMapper homeTemplateMapper;

    /**
     * 批量新增
     * @param enterpriseId
     * @param list
     */
    public void batchInsert(String enterpriseId, List<HomeTemplateDO> list){
        homeTemplateMapper.batchInsert(enterpriseId,list);
    }

    /**
     * 根据ids批量删除
     * @param enterpriseId
     * @param ids
     */
    public void deletedByIds(String enterpriseId,List<Integer> ids){
        if (CollectionUtils.isNotEmpty(ids)){
            homeTemplateMapper.deletedByIds(enterpriseId,ids);
        }
    }

    /**
     * 编辑数据
     * @param entity
     * @return
     */
    public Boolean updateById( String enterpriseId, HomeTemplateDO entity){
        homeTemplateMapper.updateById(enterpriseId,entity);
        return Boolean.FALSE;
    }

    /**
     * 根据主键id查询数据
     * @param id
     * @return
     */
    public HomeTemplateDO selectById( String enterpriseId, Integer id){
        return homeTemplateMapper.selectById(enterpriseId,id);
    }


    /**
     * 新增模板
     * @param enterpriseId
     * @param homeTemplateDO
     */
    public void insert(String enterpriseId,HomeTemplateDO homeTemplateDO){
        homeTemplateMapper.insert(enterpriseId,homeTemplateDO);
    }

    public void insertIdTemplate(String enterpriseId,HomeTemplateDO homeTemplateDO){
        homeTemplateMapper.insertIdTemplate(enterpriseId,homeTemplateDO);
    }


    /**
     * 新增模板
     * @param enterpriseId
     */
    public List<HomeTemplateDO> selectAllData(String enterpriseId,String templateName){
        return  homeTemplateMapper.selectAllData(enterpriseId,templateName);
    }


    public List<HomeTemplateDO> selectByIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Integer> ids){
        if (CollectionUtils.isNotEmpty(ids)){
            return homeTemplateMapper.selectByIds(enterpriseId,ids);
        }
        return new ArrayList<>();
    }
}
