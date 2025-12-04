package com.coolcollege.intelligent.service.user;

import com.coolcollege.intelligent.model.device.vo.LastPatrolStoreVO;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: UserCollectStoreService
 * @Description:
 * @date 2022-12-20 14:46
 */
public interface UserCollectStoreService {

    /**
     * 新增用户收藏的门店
     * @param enterpriseId
     * @param userId
     * @param storeId
     * @return
     */
    boolean addUserCollectStore(String enterpriseId, String userId, String storeId);

    /**
     * 取消收藏
     * @param enterpriseId
     * @param userId
     * @param storeId
     * @return
     */
    boolean deleteUserCollectStore(String enterpriseId, String userId, String storeId);

    /**
     * 获取用户收藏的门店
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<LastPatrolStoreVO> getUserCollectStore(String enterpriseId, String userId);

}
