package com.coolcollege.intelligent.service.pictureInspection;

import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.pictureInspection.query.StoreSceneRequest;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2021/8/26 16:20
 * @Version 1.0
 */
public interface StoreSceneService {

    /**
     * 门店场景新增
     *
     * @param enterpriseId
     * @param request
     */
    Boolean insert(String enterpriseId, StoreSceneRequest request);

    /**
     * 根据id更新门店场景信息
     *
     * @param enterpriseId
     * @param request
     */
    Boolean updateSceneById(String enterpriseId,StoreSceneRequest request);


    /**
     * 根据id删除
     *
     * @param enterpriseId
     * @param storeSceneId
     */
    Boolean deleteById(String enterpriseId,Long storeSceneId);

    /**
     * 查询门店场景列表
     *
     * @param enterpriseId
     * @return
     */
    List<StoreSceneDo> getStoreSceneList(String enterpriseId);


}
