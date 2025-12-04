package com.coolcollege.intelligent.facade.video;

import com.coolcollege.intelligent.facade.dto.ResultDTO;

/**
 * describe: 视频监控设备
 *
 * @author wangff
 * @date 2024/11/29
 */
public interface VideoDeviceFacade {

    /**
     * 检查视频监控下载中心下载状态
     * @param enterpriseId 企业id
     * @return com.coolcollege.intelligent.facade.dto.BaseResultDTO
     */
    ResultDTO checkVideoDownloadStatus(String enterpriseId);


    ResultDTO addAllStoreNode(String enterpriseId);

    ResultDTO getAllPassengerFlow(String enterpriseId, String beginTime, String endTime);
}
