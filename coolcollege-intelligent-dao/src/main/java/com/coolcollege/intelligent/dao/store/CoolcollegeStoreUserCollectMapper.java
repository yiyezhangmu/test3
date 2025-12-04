package com.coolcollege.intelligent.dao.store;

import java.util.List;
import java.util.Map;

import com.coolcollege.intelligent.model.store.StoreUserCollectDO;
import com.coolcollege.intelligent.model.store.queryDto.StoreQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
*  @author 邵凌志
*/
@Mapper
public interface CoolcollegeStoreUserCollectMapper {

    /**
     * 保存收藏信息
     * @param map
     * @return
     */
    int save(@Param("map") Map<String, Object> map);

    /**
     * 判断是否收藏
     *
     * @param map
     * @return
     */
    Boolean getInfoById(@Param("map") Map<String, Object> map);

    /**
     * 跟新手残状态
     * @param map
     * @return
     */
    int updateStatus(@Param("map") Map<String, Object> map);

    /**
     * 根据用户查询用户和门店收藏关系
     * @param enterpriseId
     * @param storeQueryDTO
     * @return
     */
    List<StoreUserCollectDO> getStoreUserCollectDo(@Param("eip") String enterpriseId, @Param("storeQueryDTO") StoreQueryDTO storeQueryDTO);

    /**
     * 删除用户下收藏的门店信息
     * @param enterpriseId
     * @param userId
     * @return
     */
    Boolean deleteStoreCollect(@Param("eip") String enterpriseId,@Param("userId")String userId);


    List<StoreUserCollectDO> listStoreUserCollect(@Param("eid") String eid,
                                                  @Param("userId") String userId,
                                                  @Param("storeIdList") List<String> storeIdList);


}