package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/2/24 16:08
 * @Version 1.0
 */
@Mapper
public interface UserRegionMappingMapper {

    /**
     * 根据主键id查询数据
     * @param id
     * @return
     */
    UserRegionMappingDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Integer id);


    /**
     * 根据人员id和部门映射id删除映射关系
     * @param enterpriseId
     * @param userId
     * @param regionIds
     */
    void deletedByUserIdAndRegionId(@Param("enterpriseId") String enterpriseId,@Param("userId") String userId,@Param("regionIds") List<String> regionIds);


    /**
     * 批量删除人员部门映射关系
     * @param enterpriseId
     * @param userIds
     * @param regionIds
     */
    void batchDeletedByUserIdsAndRegionIds(@Param("enterpriseId") String enterpriseId,@Param("userIds") List<String> userIds,@Param("regionIds") List<String> regionIds);
    /**
     * 编辑数据
     * @param entity
     * @return
     */
    Boolean updateById(@Param("enterpriseId") String enterpriseId,@Param("entity") UserRegionMappingDO entity);

    /**
     * 新增人员部门映射 返回主键id
     * @param entity
     * @return
     */
    Integer addUserRegionMapping(@Param("enterpriseId") String enterpriseId,@Param("entity") UserRegionMappingDO entity);

    /**
     * 根据用户id删除用户和区域的映射关系
     * @param enterpriseId
     * @param userIds
     */
    void deletedByUserIds(@Param("enterpriseId") String enterpriseId,@Param("userIds") List<String> userIds);

    /**
     * 根据用户id删除除用户自建外的映射关系
     * @param enterpriseId 企业id
     * @param userIds 用户id列表
     */
    void deletedExcludeCreateByUserIds(@Param("enterpriseId") String enterpriseId,@Param("userIds") List<String> userIds);

    /**
     * 根据ids批量删除
     * @param enterpriseId
     * @param ids
     */
    void deletedByIds(@Param("enterpriseId") String enterpriseId,@Param("ids") List<Integer> ids);

    /**
     * 批量插入用户和区域映射关系
     * @param enterpriseId
     * @param userRegionMappingDOS
     */
    void batchInsertRegionMapping(@Param("enterpriseId") String enterpriseId,@Param("userRegionMappingDOS") List<UserRegionMappingDO> userRegionMappingDOS);
    /**
     * 批量新增人员部门映射关系
     * @param enterpriseId
     * @param list
     */
    void batchInsert(@Param("enterpriseId") String enterpriseId,@Param("list") List<UserRegionMappingDO> list);

    /**
     * 根据人员id列表查询人员所属部门
     * @param enterpriseId
     * @param userIds
     * @return
     */
    List<UserRegionMappingDO> listUserRegionMappingByUserId(@Param("enterpriseId") String enterpriseId, @Param("userIds") List<String> userIds);

    /**
     * 查询部门下的人员
     * @param enterpriseId
     * @param regionIds
     * @return
     */
    List<UserRegionMappingDO> selectUserListByRegionIds(@Param("enterpriseId") String enterpriseId, @Param("regionIds") List<String> regionIds);

    /**
     * 查询指定部门人员数量
     * @param enterpriseId
     * @param regionIds
     * @return
     */
    Integer selectUserCountByRegionIds(@Param("enterpriseId") String enterpriseId, @Param("regionIds") List<Long> regionIds);

    /**
     * 获取区域的直连人员数量
     * @param enterpriseId
     * @param regionIds
     * @return
     */
    List<HashMap<String,Long>> getRegionUserCount(@Param("enterpriseId") String enterpriseId, @Param("regionIds") List<String> regionIds);

    /**
     * 根据Userids和regionids查询数据
     * @param enterpriseId
     * @param userIds
     * @param regionIds
     * @return
     */
    List<UserRegionMappingDO> listByUserIdsAndRegionIds(@Param("enterpriseId") String enterpriseId,@Param("userIds") List<String> userIds,@Param("regionIds") List<String> regionIds);

    /**
     * 获取用户属于哪些部门
     * @param enterpriseId
     * @param userIds
     * @return
     */
    List<UserRegionMappingDO> getRegionIdsByUserIds(@Param("enterpriseId") String enterpriseId,@Param("userIds") List<String> userIds);

    /**
     * 获取部门有哪些用户
     * @param enterpriseId
     * @param regionIds
     * @return
     */
    List<String> getUserIdsByRegionIds(@Param("enterpriseId") String enterpriseId,@Param("regionIds") List<String> regionIds);

    List<UserRegionMappingDO> getRegionIdsNoBaseNodeByUserIds(@Param("enterpriseId") String enterpriseId,@Param("userIds") List<String> userIds);
}
