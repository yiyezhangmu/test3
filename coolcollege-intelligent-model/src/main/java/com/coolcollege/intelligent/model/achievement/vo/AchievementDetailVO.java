package com.coolcollege.intelligent.model.achievement.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/26
 */
@Data
public class AchievementDetailVO {


    /**
     * 主键
     */
    private Long id;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    @Excel(name = "门店", width = 20, orderNum = "4")
    private String storeName;

    /**
     * 门店编号
     */
    @Excel(name = "门店编号", width = 20, orderNum = "5")
    private String storeNum;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 所属区域
     */
    @Excel(name = "所属区域", width = 20, orderNum = "6")
    private String regionName;

    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 模板Id
     */
    private Long formworkId;

    /**
     * 模板名称
     */
    @Excel(name = "业绩模板", width = 20, orderNum = "1")
    private String formworkName;

    /**
     * 业绩类型id
     */
    private Long achievementTypeId;

    /**
     * 业绩类型名称
     */
    @Excel(name = "业绩类型", width = 20, orderNum = "2")
    private String achievementTypeName;

    /**
     * 时间
     */
    @Excel(name = "时间", width = 20, orderNum = "3")
    private String queryTimeStr;

    /**
     * 业绩值
     */
    @Excel(name = "合计", width = 20, orderNum = "7")
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
     * 上报时间
     */
    private Date editTime;
    /**
     * 业绩产生时间
     */
    private Date produceTime;

    private Date beginTime;

    private Date endTime;

    @ApiModelProperty("商品型号")
    private String goodsType;

    @ApiModelProperty("拓展字段")
    private String extendParam;


}
