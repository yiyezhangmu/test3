package com.coolcollege.intelligent.mapper.homeTemplate;

import com.coolcollege.intelligent.dao.homeTemplate.HomeTemplateRoleMappingMappper;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateRoleMappingDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 14:57
 * @Version 1.0
 */
@Service
public class HomeTemplateRoleMappingDAO {

    @Resource
    HomeTemplateRoleMappingMappper homeTemplateRoleMappingMappper;

    /**
     * 批量新增
     * @param enterpriseId
     * @param list
     */
    public void batchInsert(String enterpriseId,List<HomeTemplateRoleMappingDO> list){
       if (CollectionUtils.isNotEmpty(list)){
           homeTemplateRoleMappingMappper.batchInsert(enterpriseId,list);
       }
    }

    /**
     * 根据ids批量删除
     * @param enterpriseId
     * @param ids
     */
    public void deletedByIds(@Param("enterpriseId") String enterpriseId,@Param("ids") List<Integer> ids){
        if (CollectionUtils.isNotEmpty(ids)){
            homeTemplateRoleMappingMappper.deletedByIds(enterpriseId,ids);
        }
    }

    /**
     * 编辑数据
     * @param entity
     * @return
     */
    public Boolean updateById(@Param("enterpriseId") String enterpriseId,@Param("entity") HomeTemplateRoleMappingDO entity){
        homeTemplateRoleMappingMappper.updateById(enterpriseId,entity);
        return Boolean.TRUE;
    }


    /**
     * 根据主键id查询数据
     * @param id
     * @return
     */
    public HomeTemplateRoleMappingDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Integer id){

        return null;
    }


    /**
     * 删除角色对应的模板映射
     * @param enterpriseId
     * @param ids
     */
    public void deletedByRoleIds(@Param("enterpriseId") String enterpriseId,@Param("ids") List<Long> ids){
        if (CollectionUtils.isNotEmpty(ids)){
            homeTemplateRoleMappingMappper.deletedByRoleIds(enterpriseId,ids);
        }
    }

    /**
     * 删除模板对应的映射关系
     * @param enterpriseId
     * @param templateId
     */
    public void deletedByTemplateId(@Param("enterpriseId") String enterpriseId,Integer templateId){
        if (templateId!=null){
            homeTemplateRoleMappingMappper.deletedByTemplateId(enterpriseId,templateId);
        }
    }

    /**
     * 根据TemplateId查询数据
     * @param ids
     * @return
     */
    public List<HomeTemplateRoleMappingDO> selectByHomeTemplateId(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Integer> ids){
        if (CollectionUtils.isNotEmpty(ids)){
            return homeTemplateRoleMappingMappper.selectByHomeTemplateId(enterpriseId,ids);
        }
        return new ArrayList<>();
    }

    /**
     * 根据RoleIds查询数据
     * @param ids
     * @return
     */
    public List<HomeTemplateRoleMappingDO> selectByRoleIds(@Param("enterpriseId") String enterpriseId, @Param("ids") List<Long> ids){
        if (CollectionUtils.isNotEmpty(ids)){
            return homeTemplateRoleMappingMappper.selectByRoleIds(enterpriseId,ids);
        }
        return new ArrayList<>();
    }
}
