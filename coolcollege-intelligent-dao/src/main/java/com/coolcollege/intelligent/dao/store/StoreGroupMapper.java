package com.coolcollege.intelligent.dao.store;

import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.queryDto.StoreGroupQueryDTO;
import com.coolcollege.intelligent.model.store.vo.StoreGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StoreGroupMapper {
    /**
     *
     * @param enterpriseId 企业id
     * @param storeGroupDOS 门店标签信息
     * @return Integer
     * @Title insertStoreLabel
     * @Description 新增门店分组
     */
     Integer insertStoreGroup(@Param("enterpriseId")String enterpriseId, @Param("storeLabelDO") StoreGroupDO storeGroupDOS);

    /**
     *
     * @param enterpriseId 企业id
     * @param storeGroupDOS 门店标签信息
     * @param updateTime 创建标签时间
     * @param updateUser 创建人
     * @return Intege
     * @Title updateStoreLabel
     * @Description 修改门店分组
     */
     Integer updateStoreLabel(@Param("enterpriseId")String enterpriseId, @Param("storeLabelDO") List<StoreGroupDO> storeGroupDOS, @Param("updateTime") Long updateTime, @Param("updateUser")String updateUser);

    /**
     *
     * @param enterpriseId
     * @param groupId
     * @return void
     * @Title deleteStoreLabelByLabelName
     * @Description 根据分组名称删除标签
     */
    void deleteStoreGroup(@Param("enterpriseId")String enterpriseId, @Param("groupId") String groupId);

    /**
     * 根据门店id获取分组
     * @param eId 企业id
     * @param sId 门店id
     * @return
     */
    List<StoreGroupDO> getStoreGroupDOs(@Param("eId") String eId,@Param("sId") String sId);

    List<StoreGroupQueryDTO> getStoreIdbyGroupIds(@Param("eId") String eId, @Param("gId") String groupId);

    Integer insertGroup(@Param("eId") String eId, @Param("entity") StoreGroupDO storeGroupDO);

    Integer updateStoreGroup(@Param("eId") String eId, @Param("entity") StoreGroupDO storeGroupDO);

    List<StoreGroupDO> getAllStoreGroupDOs(@Param("eId")String eId,@Param("groupName")String groupName);

    Integer getAllStoreGroupCount(@Param("eId")String eId,@Param("groupName")String groupName);

    List<StoreGroupDO> listStoreGroup(@Param("eId") String enterpriseId);

    StoreGroupDO getGroupByGroupId(@Param("eId") String eId, @Param("groupId") String groupId);

    StoreGroupDO getGroupByGroupName(@Param("eId") String eId, @Param("groupName") String groupName);

    /**
     * 批量删除分组
     * @param enterpriseId
     * @param groupIdList
     */
    void batchDeleteStoreGroup(@Param("eId") String enterpriseId, @Param("groupIdList") List<String> groupIdList);


    List<StoreGroupVO> getAllStoreGroupVOs(@Param("eId")String eId,@Param("groupName")String groupName);

    List<StoreGroupDO> getListByIds(@Param("eId")String eId,@Param("groupIdList")List<String> groupIdList);

    /**
     * 根据来源查询分组
     * @param eid
     * @param source
     * @return
     */
    List<StoreGroupDO> selectGroupBySource(@Param("enterpriseId") String eid, @Param("source") String source);

    /**
     * 批量新增
     * @param eid
     * @param groups
     */
    void batchInsertGroup(@Param("enterpriseId")String eid, @Param("groups")List<StoreGroupDO> groups);

    /**
     * 批量更新
     * @param eid
     * @param groups
     */
    void batchUpdateGroup(@Param("enterpriseId")String eid, @Param("groups")List<StoreGroupDO> groups);
}
