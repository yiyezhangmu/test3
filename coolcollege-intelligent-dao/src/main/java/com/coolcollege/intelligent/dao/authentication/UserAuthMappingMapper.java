package com.coolcollege.intelligent.dao.authentication;

import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 人员权限映射表
 *
 * @author zyp
 * @Description 人员权限映射
 */
@Mapper
public interface UserAuthMappingMapper {

    /**
     * 插入一条数据
     * @param eid
     * @param auth
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/26 10:47
     */
    void insertUserAuthMapping(@Param("eid") String eid, @Param("auth") UserAuthMappingDO auth);

    /**
     * 批量插入
     *
     * @param eid
     * @param list
     */
    void batchInsertUserAuthMapping(@Param("eid") String eid,
                                    @Param("list") List<UserAuthMappingDO> list);

    /**
     * 根据UserId获取权限
     *
     * @param eid
     * @param userId
     * @return
     */
    List<UserAuthMappingDO> listUserAuthMappingByUserId(@Param("eid") String eid,
                                                        @Param("userId") String userId);

    List<String> getMappingUserAuthMappingByUserId(@Param("eid") String eid,
                                                        @Param("userId") String userId);


    /**
     * 根据UserId获取权限
     *
     * @param eid
     * @param userId
     * @return
     */
    int countUserAuthCountByUserIdAndStoreId(@Param("eid") String eid,
                                                        @Param("userId") String userId,@Param("storeId") String storeId);



    /**
     * 根据UserId获取权限
     *
     * @param eid
     * @param userId
     * @return
     */
    int countUserAuthCountByUserIdAndRegionId(@Param("eid") String eid,
                                             @Param("userId") String userId,@Param("regionIds") List<String> regionIds);
    /**
     * 查询用户区域
     * @param eid
     * @param userList
     * @return
     */
    List<UserAuthMappingDO> listUserAuthMappingByUserIdList(@Param("eid") String eid,
                                                                   @Param("userList") List<String> userList);

    List<UserAuthMappingDO> listUserAuthMappingByMappingList(@Param("eid") String eid,
                                                             @Param("mappingIdList") List<String> mappingIdList,
                                                             @Param("type") String type);

    /**
     *查询不包含可视化范围的区域人员
     * @param eid
     * @param mappingIdList
     * @return
     */
    List<UserAuthMappingDO> listUserAuthMappingByAuth(@Param("eid") String eid,
                                                      @Param("type") String type,
                                                      @Param("mappingIdList") List<String> mappingIdList,
                                                      @Param("positionType")String positionType,
                                                      @Param("notRoleAuth")String notRoleAuth);


    List<UserAuthMappingDO> listUserAuthMappingByUserList(@Param("eid") String eid,
                                                          @Param("userIdList") List<String> userIdList);


    List<UserAuthMappingDO> listUserAuthMappingByUserAndType(@Param("eid") String eid,
                                                             @Param("userId") String userId,
                                                             @Param("type") String type);

    void deleteAuthMappingByIdAndType(@Param("eid") String eid,
                                      @Param("ids") List<String> ids,
                                      @Param("type") String  type);


    /**
     * 根据userId批量删除
     * @param eid
     * @param userIds
     */
    void deleteAuthMappingByUserIds(@Param("eid") String eid, @Param("userIds") List<String> userIds);

    /**
     * 根据userId和来源批量删除
     * @param eid
     * @param userIds
     * @param source
     * @author: xugangkun
     * @return void
     * @date: 2021/10/12 16:21
     */
    void deleteAuthMappingByUserIdAndSource(@Param("eid") String eid, @Param("userIds") List<String> userIds, @Param("source") String source);

    /**
     * 根据用户id列表和权限类型删除
     * @Param:
     * @param eid
     * @param userIds
     * @param type
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/24 11:33
     */
    void deleteAuthMappingByUserIdsAndType(@Param("eid") String eid, @Param("userIds") List<String> userIds, @Param("type") String type);

    void deleteAuthMappingByUserIdAndTypeAndMappingIds(@Param("eid") String eid, @Param("userId") String userId, @Param("type") String type, @Param("mappingIds") List<String> mappingIds);

    /**
     * 根据id(主键)删除
     * @Param:
     * @param eid
     * @param ids
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/24 11:33
     */
    void deleteAuthMappingByIds(@Param("eid") String eid, @Param("ids") List<Long> ids);

    /**
     * 根据用户id获得权限映射信息主键列表
     * @param eid
     * @param userId
     * @return: java.util.List<java.lang.Long>
     * @Author: xugangkun
     * @Date: 2021/3/25 14:24
     */
    List<Long> selectIdsByUserId(@Param("eid") String eid, @Param("userId") String userId);


    List<Long> selectIdsByUserIds(@Param("eid") String eid, @Param("userIds") List<String> userIds);

    UserAuthMappingDO getUserAuthByUserIdAndMappingId(@Param("eid") String eid,
                                                             @Param("userId") String userId,
                                                             @Param("mappingId") String mappingId,
                                                             @Param("type") String type);

    /**
     * 区域转门店
     * @param eid
     * @return
     */
    Long changeRegionToStoreAuth(@Param("eid") String eid);

    List<String> getUserIdsByMappingIds(@Param("enterpriseId") String enterpriseId, @Param("mappingIdList") List<String> mappingIdList);

    List<UserAuthMappingDO> getUserAuthByMappingIds(@Param("enterpriseId") String enterpriseId, @Param("mappingIdList") List<String> mappingIdList);

    List<String> getMappingIdsByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId")String userId);


    List<String> getRegionIdByUserId(@Param("enterpriseId") String enterpriseId,
                                     @Param("userId") String userId);

    /**
     * 根据UserId和权限来源获取权限
     * @param eid
     * @param userIdList
     * @param source
     * @return
     */
    List<UserAuthMappingDO> listByUserIdListAndSource(@Param("eid") String eid, @Param("userIdList") List<String> userIdList, @Param("source") String source);

    List<UserAuthMappingDO> getAllByUserIds(@Param("enterpriseId") String enterpriseId,
                                            @Param("userIdsByRoleIdList") List<String> userIdsByRoleIdList);
}