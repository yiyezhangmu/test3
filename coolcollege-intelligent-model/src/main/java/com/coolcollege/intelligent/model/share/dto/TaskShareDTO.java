package com.coolcollege.intelligent.model.share.dto;

import com.coolcollege.intelligent.model.share.TaskShareDO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TaskShareDTO {
    /**
     * 分享人姓名
     */
    @JsonProperty("share_user_name")
    private String shareUserName;

    /**
     * 分享时间
     */
    @JsonProperty("share_time")
    private String shareTime;


    /**
     * 门店名称
     */
    @JsonProperty("store_name")
    private String storeName;

    /**
     * 分享详情
     */
    @JsonProperty("task_share")
    private TaskShareDO taskShare;


    /**
     * 头像
     */
    private String avatar;

    /**
     * cform对应id
     */
    private String cId;

    /**
     * 分享类型
     */
    @JsonProperty("share_type")
    private String shareType;

    /**
     * 分享类型
     */
    @JsonProperty("share_name")
    private String shareName;

    /**
     * cform表单
     */
    private String bizCode;

    /**
     * 检查项实体类
     */
    @JsonProperty("check_table_list")
    private List<CheckTableDO> checkTableList;

    /**
     * 分享id，做分页主键用，不返回
     */
    @JsonIgnore
    private String shareId;
}
