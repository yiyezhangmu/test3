package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolStoreCloudDO;
import com.coolcollege.intelligent.model.patrolstore.request.AddPatrolStoreCloudRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolStoreCloudVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

/**
 * @Author: hu hu
 * @Date: 2024/11/27 14:00
 * @Description: 云图库
 */
public interface PatrolStoreCloudService {

    /**
     * 新增或更新云图库
     *
     * @param enterpriseId 企业id
     * @param param        云图库信息
     * @param currentUser  当前用户
     * @return 结果
     */
    Long insertOrUpdate(String enterpriseId, AddPatrolStoreCloudRequest param, CurrentUser currentUser);

    /**
     * 获取云图库信息
     *
     * @param enterpriseId 企业id
     * @param businessId   巡店id
     * @param userId       用户id
     * @return 云图库信息
     */
    PatrolStoreCloudVO getCloudByBusinessId(String enterpriseId, Long businessId, String userId);

    /**
     * 删除云图库
     *
     * @param enterpriseId 企业id
     * @param businessId   巡店id
     * @param userId       用户id
     * @return 删除结果
     */
    Integer deleteCloud(String enterpriseId, Long businessId, String userId);

    /**
     * 根据id查询云图库
     *
     * @param enterpriseId 企业id
     * @param id           主键
     * @return 云图库信息
     */
    TbPatrolStoreCloudDO selectById(String enterpriseId, Long id);

    /**
     * 更新视频信息
     *
     * @param enterpriseId 企业id
     * @param id           主键
     * @param video        视频信息
     */
    void updateVideo(String enterpriseId, Long id, String video);
}
