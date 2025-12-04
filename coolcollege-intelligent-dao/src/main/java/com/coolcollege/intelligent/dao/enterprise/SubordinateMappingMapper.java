package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.SubordinateMappingDO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/2/24 16:08
 * @Version 1.0
 */
@Mapper
public interface SubordinateMappingMapper {

    /**
     * 根据主键id查询数据
     * @param id
     * @return
     */
    SubordinateMappingDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Integer id);

    /**
     * 根据主键删除数据
     * @param ids
     */
    void deletedByIds(@Param("enterpriseId") String enterpriseId,@Param("ids") List<Integer> ids);

    /**
     * 根据主键删除数据
     * @param id
     */
    void deletedById(@Param("enterpriseId") String enterpriseId,@Param("id") Integer id);

    /**
     * 编辑数据
     * @param entity
     * @return
     */
    Boolean updateById(@Param("enterpriseId") String enterpriseId,@Param("entity") SubordinateMappingDO entity);

    /**
     * 新增人员部门映射 返回主键id
     * @param entity
     * @return
     */
    Integer addSubordinateMapping(@Param("enterpriseId") String enterpriseId,@Param("entity") SubordinateMappingDO entity);

    /**
     * 根据用户的ids删除用户的下属部门
     * @param enterpriseId
     * @param userIds
     */
    void deletedByUserIds(@Param("enterpriseId") String enterpriseId,@Param("userIds") List<String> userIds);

    /**
     * 批量新增用户的下属部门
     * @param enterpriseId
     * @param subordinateMappingDOS
     */
    void batchInsertSubordinateMapping(@Param("enterpriseId") String enterpriseId,@Param("subordinateMappingDOS") List<SubordinateMappingDO> subordinateMappingDOS);

    /**
     * 查询我的直属上级
     * @param enterpriseId
     * @param userId
     * @return
     */
    SubordinateMappingDO selectByUserIdAndType(@Param("enterpriseId") String enterpriseId,
                                               @Param("userId") String userId);

    /**
     * 更新直属上级人员ID
     * @param enterpriseId
     * @param userId
     * @param personalId
     * @param currentUserId
     */
    void updateByUserIdAndType(@Param("enterpriseId") String enterpriseId,
                               @Param("userId") String userId,
                               @Param("personalId") String personalId,
                               @Param("currentUserId") String currentUserId);

    /**
     * 根据用户ids查询直属上级列表
     * @param enterpriseId
     * @param userIds
     * @return
     */
    List<SubordinateMappingDO> selectByUserIds(@Param("enterpriseId") String enterpriseId, @Param("userIds") List<String> userIds);

    /**
     * 根据用户的ids删除用户的下属部门
     * @param enterpriseId
     * @param userIds
     */
    void deletedByUserIdsAndType(@Param("enterpriseId") String enterpriseId,@Param("userIds") List<String> userIds);

    /**
     * 批量查询直属上级
     * @param enterpriseId
     * @param userIds
     * @return
     */
    List<SubordinateMappingDO> selectByUserIdsAndType(@Param("enterpriseId") String enterpriseId, @Param("userIds") List<String> userIds);

}
