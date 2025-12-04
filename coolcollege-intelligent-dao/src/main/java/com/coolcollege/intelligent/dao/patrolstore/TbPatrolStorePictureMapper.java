package com.coolcollege.intelligent.dao.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.TbPatrolStorePictureDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author byd
 */
@Mapper
public interface TbPatrolStorePictureMapper {

    /**
     * 数据插入
     * @return
     */
    Integer insert(@Param("enterpriseId") String enterpriseId, @Param("picture") TbPatrolStorePictureDO tbPatrolStorePicture);

    /**
     * 数据插入
     * @return
     */
    Integer batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbPatrolStorePictureDO> list);

    List<TbPatrolStorePictureDO> getStoreScenePictureList(@Param("enterpriseId") String enterpriseId, @Param("businessId") Long businessId,
                                                          @Param("storeSceneId") Long storeSceneId);

    List<TbPatrolStorePictureDO> selectByBusinessIdList(@Param("enterpriseId") String enterpriseId,@Param("businessIdList") List<Long> businessIdList);

    Long selectIdOne(@Param("enterpriseId") String enterpriseId,@Param("businessId") Long businessId);

    Long selectPictureIdOne(@Param("enterpriseId") String enterpriseId,@Param("businessId") Long businessId, @Param("storeSceneId") Long storeSceneId);

}
