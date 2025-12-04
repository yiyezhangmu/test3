package com.coolcollege.intelligent.model.achievement.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2024-03-16 01:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementTaskRecordDO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("上传时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date editTime;

    @ApiModelProperty("子任务审批链开始时间")
    private Date subBeginTime;

    @ApiModelProperty("子任务审批链结束时间")
    private Date subEndTime;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("循环任务的循环批次")
    private Long loopCount;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("实际上报人id")
    private String produceUserId;

    @ApiModelProperty("实际上报人名称")
    private String produceUserName;

    @ApiModelProperty("删除标识，0：未删除，1：已删除")
    private Boolean deleted;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("预计下架时间")
    private Date planDelistTime;

    @ApiModelProperty("库存")
    private Integer goodsNum;

    @ApiModelProperty("现场拍照，逗号分隔")
    private String picture;

    @ApiModelProperty("商品品类Code")
    private String productCategory;

    @ApiModelProperty("商品中类")
    private String productMiddleClass;

    @ApiModelProperty("商品型号")
    private String productType;

    @ApiModelProperty("任务类型   新品上架 ACHIEVEMENT_NEW_RELEASE\n" +
            "   老品下架 ACHIEVEMENT_OLD_PRODUCTS_OFF")
    private String taskType;

    @ApiModelProperty("门店任务id")
    private Long taskStoreId;

    @ApiModelProperty("提交时间")
    private Date submitTime;

    @ApiModelProperty("品类名称")
    private String productCategoryName;

    @ApiModelProperty("中类Name")
    private String productMiddleClassName;

    @ApiModelProperty("小类Code")
    private String productSmallCategoryCode;

    @ApiModelProperty("小类Name")
    private String productSmallCategoryName;

    @ApiModelProperty("型号码")
    private String productModelNumber;

    @ApiModelProperty("出样时间")
    private Date planGoodTime;

}