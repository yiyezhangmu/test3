package com.coolcollege.intelligent.dao.store;

import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.StoreGroupMappingDO;
import com.coolcollege.intelligent.model.store.dto.StoreGroupDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StoreGroupMappingMapper {
    /**
     *
     * @param enterpriseId 企业id
     * @param entity 门店id集合
     * @return Integer
     * @Description 门店关联标签
     */
    Integer insertStoreGroupMapping(@Param("enterpriseId")String enterpriseId,@Param("storeGroupMappingDO") StoreGroupMappingDO entity);

    /**
     *
     * @param enterpriseId 企业id
     * @param storeGroupMappingDO 门店标签映射信息
     * @return Integer
     * @Description 修改门店关联标签
     */
    Integer updateStoreGroupMapping(@Param("enterpriseId")String enterpriseId, @Param("storeLabelMappingDO") StoreGroupMappingDO storeGroupMappingDO);

    /**
     *
     * @param enterpriseId 企业id
     * @param groupId 组别id
     * @return void
     * @Description 删除门店关联标签
     */
    void deleteStoreGroupMappingByGroupId(@Param("enterpriseId")String enterpriseId, @Param("groupId") String groupId);

    /**
     * 根据组别Id获取门店id
     * @param enterpriseId
     * @param groupId
     * @return
     */
    List<String> selectStoreByGroupId(@Param("enterpriseId")String enterpriseId, @Param("groupId") String groupId);

    /**
     *
     * @param enterpriseId 企业id
     * @param storeId 门店标签映射信息
     * @return Integer
     * @Description 删除门店关联标签
     */
    List<StoreGroupDO> selectGroupsByStoreId(@Param("enterpriseId")String enterpriseId, @Param("storeId") String storeId);

    /**
     * 获取企业所有的组别映射信息
     * @param enterpriseId
     * @return
     */
    List<StoreGroupMappingDO> getAllGroupMapping(@Param("enterpriseId")String enterpriseId,@Param("storeIds") List<String> storeIds);

    List<StoreGroupMappingDO> getStoreGroupMappingByGroupIDs(@Param("enterpriseId")String enterpriseId,@Param("groupIds")List<String> groupIds);

    List<StoreGroupDTO> selectStoreGroupDTO(@Param("eId") String eId, @Param("groupIds") List<String> groupIds, @Param("storeIds") List<String> storeIdList);

    Integer batchInsertMapping(@Param("eId") String eId, @Param("storeIdList") List<String> storeIdList, @Param("groupId") String groupId);

    Integer deleteMappingByStoreId(@Param("eId") String eId, @Param("storeId") String storeId);

    Integer deleteMappingByStoreIdList(@Param("eId") String eId, @Param("storeIds") List<String> storeIds);

    Integer insertGroupMappingList(@Param("eId") String eId, @Param("storeMappingIdList") List<StoreGroupMappingDO> storeGroupMappingDOList);

    Integer batchDeleteMappingByGroupIdList(@Param("eId") String eId,@Param("storeGroupIdList") List<String> storeGroupIdList);

    List<StoreGroupMappingDO> selectMappingByStoreIds(@Param("enterpriseId") String enterpriseId, @Param("storeIds") List<String> storeIds);

    List<String> selectStoreIdByGroupId(@Param("enterpriseId") String enterpriseId, @Param("groupId") String groupId);

    List<String> selectAuthStoreIdByGroupId(@Param("enterpriseId") String enterpriseId,
                                            @Param("groupId") String groupId,
                                            @Param("isAdmin") Boolean isAdmin,
                                            @Param("authStoreIdList") List<String> authStoreIdList,
                                            @Param("authFullRegionPathList") List<String> authFullRegionPathList);

    /**
     * 根据门店id列表和分组id删除
     * @param eid
     * @param groupId
     * @param storeDeptIdList
     * @author: xugangkun
     * @return void
     * @date: 2022/5/18 17:06
     */
    void deleteByGroupIdAndStoreIdList(@Param("eid") String eid, @Param("groupId") String groupId, @Param("storeDeptIdList") List<String> storeDeptIdList);

}
