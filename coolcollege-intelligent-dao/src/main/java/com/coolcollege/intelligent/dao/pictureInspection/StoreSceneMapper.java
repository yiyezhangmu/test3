package com.coolcollege.intelligent.dao.pictureInspection;

import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/8/26 18:06
 * @Version 1.0
 */
@Mapper
public interface StoreSceneMapper {
    /**
     * 门店场景新增
     *
     * @param enterpriseId
     * @param storeSceneDo
     */
    void insert(@Param("enterpriseId") String enterpriseId, @Param("storeSceneDo") StoreSceneDo storeSceneDo);


    /**
     * 根据id更新门店场景信息
     *
     * @param enterpriseId
     * @param storeSceneId
     * @param storeSceneName
     */
    void updateSceneById(@Param("enterpriseId") String enterpriseId,
                         @Param("storeSceneId") Long storeSceneId,
                         @Param("storeSceneName") String storeSceneName,
                         @Param("sceneType") String sceneType);


    /**
     * 根据id删除
     *
     * @param enterpriseId
     * @param storeSceneId
     */
    void deleteById(@Param("enterpriseId") String enterpriseId, @Param("storeSceneId") Long storeSceneId);

    /**
     * 查询门店场景列表
     *
     * @param enterpriseId
     * @return
     */
    List<StoreSceneDo> getStoreSceneList(@Param("enterpriseId") String enterpriseId);

    StoreSceneDo getStoreSceneByName(@Param("enterpriseId") String enterpriseId, @Param("name") String name);

    StoreSceneDo getStoreSceneById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    /**
     * 查询门店场景列表
     *
     * @param enterpriseId
     * @return
     */
    List<StoreSceneDo> getStoreSceneListForName(@Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> idList);

    void realDeleteById(@Param("enterpriseId") String enterpriseId,@Param("idList") List<Long> idList);

}
