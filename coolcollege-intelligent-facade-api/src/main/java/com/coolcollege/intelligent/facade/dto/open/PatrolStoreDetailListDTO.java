package com.coolcollege.intelligent.facade.dto.open;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatrolStoreDetailListDTO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 巡店记录id
     */
    private Long businessId;

    /**
     * 自定义名称
     */
    private String columnName;

    private String categoryName;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 检查表名称
     */
    private String metaTableName;

    /**
     * 默认分值
     */
    private BigDecimal checkScore;

    /**
     * 结果
     */

    private String checkResult;

    /**
     * 结果
     */
    private String checkResultName;


    /**
     * 检查项分值
     */
    private BigDecimal supportScore;

    /**
     * 处罚金额
     */
    private BigDecimal punishMoney;

    /**
     * 奖励金额
     */
    private BigDecimal awardMoney;

    /**
     * 标准图
     */
    private String standardPic;


    private Date createTime;

    private String supervisorName;

    /**
     * 巡店人id
     */
    private String supervisorId;


    private String checkPics;

    /**
     * 备注
     */
    private String checkText;

    /**
     * 子任务审批链开始时间
     */

    private Date subBeginTime;
    /**
     * 子任务审批链结束时间
     */
    private Date subEndTime;


    private String value;


    private String description;

    /**
     * 实际奖赏
     */
    private String checkAwardPunish;

    /**
     * 奖惩（标准）
     */
    private String awardPunish;



    private String format;

    private Integer tableProperty;

    public String getCheckResultName(){
        if(StringUtils.isNotBlank(this.checkResultName)){
            return this.checkResultName;
        }
        if(StringUtils.isNotBlank(this.checkResult)){
            if("FAIL".equals(this.checkResult)){
                return "不合格";
            }else if("INAPPLICABLE".equals(this.checkResult)){
                return "不适用";
            }else if("PASS".equals(this.checkResult)){
                return "合格";
            }
        }
        return null;
    }

    private static final long serialVersionUID = 1L;

    private String checkVideo;

}