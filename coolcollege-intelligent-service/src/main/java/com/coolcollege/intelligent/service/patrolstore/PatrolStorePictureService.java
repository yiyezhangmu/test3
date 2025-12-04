package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.TbPatrolStorePictureDO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStoreCapturePictureDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbPatrolStorePictureDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.TbPatrolStorePictureVO;

import java.util.List;

/**
 * @author byd
 * @date 2021-08-27 13:49
 */
public interface PatrolStorePictureService {

    /**
     * 上传抓拍图片
     */
    void uploadPicture(String eid, TbPatrolStorePictureDTO pictureDTO);

    /**
     *
     * @param eid
     * @param storeSceneId
     * @param businessId
     */
    TbPatrolStorePictureVO getStoreScenePictureList(String eid, Long businessId, Long storeSceneId);

    /**
     * 抓拍图片
     * @param businessId 巡店记录id
     */
    void capturePicture(String eid, Long businessId, Long storeSceneId,String patrolType);

    /**
     * 开始抓拍图片 异步抓拍
     * @param param
     */
    void beginCapturePicture(String eid, TbPatrolStoreCapturePictureDTO param);

    /**
     * 开始抓拍图片 异步抓拍
     * @param param
     */
    TbPatrolStorePictureDO beginCapturePictureByDevice(String eid, TbPatrolStoreCapturePictureDTO param);
}
