package com.coolcollege.intelligent.model.achievement.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/28
 */
@Data
public class AchievementStoreDetailVO {


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
    @Excel(name = "门店名称", width = 20, orderNum = "1")
    private String storeName;

    /**
     * 门店编号
     */
    @Excel(name = "门店编号", width = 20, orderNum = "2")
    private String storeNum;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 所属区域
     */
    @Excel(name = "所属区域", width = 20, orderNum = "3")
    private String regionName;


    private List<AchievementFormworkDetailVO> formworkDetailVOList;


    private List<AchievementProduceUserVO> produceUserVOList;

    @Excel(name = "合计", width = 20, orderNum = "4",type = 10)
    private BigDecimal completionTarget;

    @Excel(name = "各类型业绩详情", width = 40, orderNum = "5")
    private String detail;

    @Excel(name = "业绩产生人", width = 20, orderNum = "6",type = 10)
    private Integer produceUserNum;

    @Excel(name = "产生人详情", width = 40, orderNum = "7")
    private String produceUserDetail;
}
