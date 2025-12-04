package com.coolcollege.intelligent.service.picture;

import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreCheckQuery;
import com.coolcollege.intelligent.model.picture.PictureCenterStoreDO;
import com.coolcollege.intelligent.model.picture.query.PictureCenterQuery;
import com.coolcollege.intelligent.model.picture.vo.PictureCenterVO;
import com.coolcollege.intelligent.model.picture.vo.PictureQuestionCenterVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Description: 图片中心
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
public interface PictureCenterService {
    PageInfo<PictureCenterVO> getRecordByTaskName(String enterpriseId, PictureCenterQuery query);

    PageInfo<PictureCenterVO> getDisplayRecordByTaskName(String enterpriseId, PictureCenterQuery query);

    List<PictureCenterStoreDO> getStorePicture(String enterpriseId, PictureCenterQuery query);

    /**
     * 定时任务巡检
     * @param enterpriseId
     * @param query
     * @return
     */
    PageInfo<PictureCenterVO> getPictureRecordByTaskName(String enterpriseId, PictureCenterQuery query);

    /**
     * 工单图片库
     * @param enterpriseId
     * @param query
     * @return
     */
    PageInfo<PictureQuestionCenterVO> taskQuestionRecord(String enterpriseId, PictureCenterQuery query);

    PageInfo<PictureCenterVO> getCheckRecordByTaskName(String enterpriseId, PatrolStoreCheckQuery query, String userId);



}
