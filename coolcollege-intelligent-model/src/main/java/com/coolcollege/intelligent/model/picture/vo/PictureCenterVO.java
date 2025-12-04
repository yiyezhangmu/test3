package com.coolcollege.intelligent.model.picture.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Description: 图片中心
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureCenterVO {

    /**
     * id
     */
    private Long id;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 检查表名称
     */
    private String metaTableName;

    /**
     * 图片集合
     */
    private List<PictureCenterColumnVO> pictureCenterColumnList;

    /**
     * 算法类型
     */
    private String aiType;

    @ApiModelProperty("表图片列表")
    private List<PictureCenterTableVO> pictureCenterTableVOList;

    private Date createTime;

    private Date completeTime;

    @ApiModelProperty("签到时间")
    private Date signStartTime;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @ApiModelProperty("巡店人姓名")
    private String supervisorName;

    @ApiModelProperty("巡店人工号")
    private String supervisorJobNum;

    private String status;

    private String patrolType;


    @ApiModelProperty("大区稽核人id")
    private String bigRegionUserId;

    @ApiModelProperty("大区稽核人姓名")
    private String bigRegionUserName;

    @ApiModelProperty("大区稽核人工号")
    private String bigRegionUserJobNum;

    @ApiModelProperty("大区稽核时间")
    private Date bigRegionCheckTime;

    @ApiModelProperty("战区稽核人id")
    private String warZoneUserId;

    @ApiModelProperty("战区稽核人姓名")
    private String warZoneUserName;

    @ApiModelProperty("战区稽核人工号")
    private String warZoneUserJobNum;

    @ApiModelProperty("战区稽核时间")
    private Date warZoneCheckTime;

    /**
     * 不合格原因
     */
    @ApiModelProperty("不合格原因")
    private String checkResultReason;

}
