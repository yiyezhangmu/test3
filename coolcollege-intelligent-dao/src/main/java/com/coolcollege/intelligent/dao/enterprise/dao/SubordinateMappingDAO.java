package com.coolcollege.intelligent.dao.enterprise.dao;

import com.coolcollege.intelligent.dao.enterprise.SubordinateMappingMapper;
import com.coolcollege.intelligent.model.enterprise.SubordinateMappingDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/2/24 16:08
 * @Version 1.0
 */
@Service
public class SubordinateMappingDAO {

    @Resource
    private SubordinateMappingMapper subordinateMappingMapper;

    /**
     * 根据主键id查询数据
     * @param id
     * @return
     */
    public SubordinateMappingDO selectById(String enterpriseId,Integer id){
        return subordinateMappingMapper.selectById(enterpriseId,id);
    }


    /**
     * 根据主键删除数据
     * @param id
     */
    public void deletedById(String enterpriseId,Integer id){
        subordinateMappingMapper.deletedById(enterpriseId,id);
    }

    public void deletedByIds(String enterpriseId,List<Integer> ids){
        if(CollectionUtils.isEmpty(ids) || StringUtils.isBlank(enterpriseId)){
            return;
        }
        subordinateMappingMapper.deletedByIds(enterpriseId, ids);
    }

    /**
     * 编辑数据
     * @param entity
     * @return
     */
    public Boolean updateById(String enterpriseId,SubordinateMappingDO entity){
        return subordinateMappingMapper.updateById(enterpriseId,entity);
    }

    /**
     * 新增人员部门映射 返回主键id
     * @param entity
     * @return
     */
    public Integer addSubordinateMapping(String enterpriseId,SubordinateMappingDO entity){
        return subordinateMappingMapper.addSubordinateMapping(enterpriseId,entity);
    }

    /**
     * 根据用户的ids删除用户的下属部门
     * @param enterpriseId
     * @param userIds
     */
    public void deletedByUserIds(String enterpriseId,List<String> userIds){
        if (CollectionUtils.isEmpty(userIds)){
            return;
        }
        List<String> distinctUserIds = userIds.stream().distinct().collect(Collectors.toList());
        subordinateMappingMapper.deletedByUserIds(enterpriseId,distinctUserIds);
    }

    /**
     * 批量新增用户的下属部门
     * @param enterpriseId
     * @param subordinateMappingDOS
     */
    public void batchInsertSubordinateMapping(String enterpriseId, List<SubordinateMappingDO> subordinateMappingDOS) {
        if (CollectionUtils.isEmpty(subordinateMappingDOS)) {
            return;
        }
        List<SubordinateMappingDO> distinctData = subordinateMappingDOS.stream()
                .distinct()
                .collect(Collectors.toList());
        subordinateMappingMapper.batchInsertSubordinateMapping(enterpriseId, distinctData);
    }

    /**
     * 查询我的直属上级
     * @param enterpriseId
     * @param userId
     * @return
     */
    public SubordinateMappingDO selectByUserIdAndType(String enterpriseId, String userId){
        return subordinateMappingMapper.selectByUserIdAndType(enterpriseId,userId);
    }

    /**
     * 更新直属上级人员ID
     * @param enterpriseId
     * @param userId
     * @param personalId
     * @param currentUserId
     */
    public void updateByUserIdAndType(String enterpriseId,String userId, String personalId, String currentUserId){
        subordinateMappingMapper.updateByUserIdAndType(enterpriseId,userId,personalId,currentUserId);
    }

    /**
     * 根据用户ids查询直属上级列表
     * @param enterpriseId
     * @param userIds
     * @return
     */
    public List<SubordinateMappingDO> selectByUserIds(String enterpriseId, List<String> userIds){
        if (CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return subordinateMappingMapper.selectByUserIds(enterpriseId,userIds);
    }


    /**
     * 根据用户的ids删除用户的直属上级
     * @param enterpriseId
     * @param userIds
     */
    public void deletedByUserIdsAndType(String enterpriseId,List<String> userIds){
        if (CollectionUtils.isEmpty(userIds)){
            return;
        }
        List<String> distinctUserIds = userIds.stream().distinct().collect(Collectors.toList());
        subordinateMappingMapper.deletedByUserIdsAndType(enterpriseId,distinctUserIds);
    }


    public List<SubordinateMappingDO> selectByUserIdsAndType(String enterpriseId, List<String> userIds){
        if (CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return subordinateMappingMapper.selectByUserIdsAndType(enterpriseId,userIds);
    }
}
