package com.coolcollege.intelligent.model.share;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class TaskShareDO {
    /**
     * 分享id
     */
    @JsonProperty("share_id")
    private String shareId;

    /**
     * 分享的类型
     */
    @JsonProperty("share_type")
    private String shareType;

    /**
     * 分享主题
     */
    @JsonProperty("share_topic")
    private String shareTopic;

    /**
     * 分享内容
     */
    @JsonProperty("share_content")
    private String shareContent;

    /**
     * 分享图片
     */
    @JsonProperty("share_picture")
    private String sharePicture;

    /**
     * 任务得分
     */
    @JsonProperty("task_score")
    private BigDecimal taskScore;

    /**
     * 分享的门店标签
     */
    @JsonProperty("share_store_label")
    private String shareStoreLabel;

    /**
     * 门店ID
     */
    @JsonProperty("store_id")
    private String storeId;

    /**
     * 可视范围
     */
    private String visibleRange;

    private Long businessId;

    /**
     * 父任务id
     */
    private Long taskId;

    /**
     * 子任务id
     */
    private Long taskSubId;

    /**
     * 分享时间
     */
    private Long createTime;

    /**
     * 分享人
     */
    private String createUser;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    private String detail;

    private Long loopCount;

}
