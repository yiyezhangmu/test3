package com.coolcollege.intelligent.model.metatable.dto;

import lombok.Data;

/**
 * @author chenyupeng
 * @since 2022/4/1
 */
@Data
public class DetectWorkWearAnalyseDTO {

    /**
     * 图片Id
     */
    private Long pictureId;

    /**
     * 图片url
     */
    private String picUrl;

    /**
     * 检查项id
     */
    private Long metaColumnId;

    /**
     * 分析结果
     */
    private String aiResult;

    /**
     * 分析内容
     */
    private String aiContent;

    /**
     * 场景id
     */
    private Long storeSceneId;

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 通道id
     */
    private Long deviceChannelId;

}
