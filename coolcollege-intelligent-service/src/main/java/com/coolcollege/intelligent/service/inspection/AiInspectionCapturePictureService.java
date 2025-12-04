package com.coolcollege.intelligent.service.inspection;

import com.coolcollege.intelligent.model.ai.dto.ShuZhiMaLiGetAiResultDTO;
import com.coolcollege.intelligent.model.device.YingShiCloudRecordingMessage;
import com.coolcollege.intelligent.model.inspection.AiInspectionQuestionCreateDTO;
import com.coolcollege.intelligent.model.inspection.AiInspectionTimePeriodDealDTO;
import com.coolcollege.intelligent.model.inspection.AiInspectionTimePeriodStoreDealDTO;

/**
 * @author byd
 * @date 2025-10-14 17:40
 */
public interface AiInspectionCapturePictureService {

    /**
     * 抓拍图片
     *
     * @param enterpriseId 企业ID
     * @param captureTime  抓拍时间
     */
    void capturePicture(String enterpriseId, String captureTime);


    /**
     * 拆解范围到门店
     *
     * @param enterpriseId
     * @param aiInspectionTimePeriodDealDTO
     */
    void decomposeStores(String enterpriseId, AiInspectionTimePeriodDealDTO aiInspectionTimePeriodDealDTO);

    /**
     * 门店进行抓拍
     * @param enterpriseId
     * @param timePeriodStoreDealDTO
     */
    void storeCapture(String enterpriseId, AiInspectionTimePeriodStoreDealDTO timePeriodStoreDealDTO);

    /**
     * 抓拍图片
     *
     * @param enterpriseId 企业ID
     */
    void getInspectionResult(String enterpriseId, Long inspectionPeriodId);

    void callBackResult(String enterpriseId, ShuZhiMaLiGetAiResultDTO aiResultDTO);

    void aiInspectionQuestionBuild(String enterpriseId, AiInspectionQuestionCreateDTO aiInspectionQuestionCreateDTO);

    /**
     * 设备回调(国标)
     */
    Boolean handelDeviceCaptureCallBack(YingShiCloudRecordingMessage recordingMessage);

    /**
     * 查询设备抓拍结果
     */
    void queryDeviceCaptureResult(String enterpriseId);
}
