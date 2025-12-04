package com.coolcollege.intelligent.model.achievement.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shuchang.wei
 * @date 2021/5/20 9:53
 */
@Data
public class AchievementDetailDO {
    private Long id;

    /**
     * 上报时间
     */
    private Date createTime;

    /**
     * 业绩产生时间
     */
    private Date produceTime;
    /**
     * 修改时间
     */
    private Date editTime;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 业绩类型id
     */
    private Long achievementTypeId;

    /**
     * 业绩值
     */
    private BigDecimal achievementAmount;

    /**
     * 上传人id
     */
    private String createUserId;

    /**
     * 上传人姓名
     */
    private String createUserName;

    /**
     * 业绩产生人id
     */
    private String produceUserId;

    /**
     * 业绩产生人姓名
     */
    private String produceUserName;

    /**
     * 删除标识
     */
    private Boolean deleted;

    /**
     * 模板id
     */
    private Long achievementFormworkId;

    /**
     * 模板类型
     */
    private String achievementFormworkType;

    @ApiModelProperty("商品型号")
    private String goodsType;

    @ApiModelProperty("拓展字段")
    private String extendParam;

    private String goodsNum;

    private String mainClass;

}
