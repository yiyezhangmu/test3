package com.coolcollege.intelligent.model.metatable.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author yezhe
 * @date 2021-01-22 14:09
 */

/**
 * 标准检查项评价项配置表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbMetaColumnResultDTO implements Serializable {

    /**
     * 自定义名称
     */
    private Long id;

    /**
     * 自定义名称
     */
    private String resultName;

    /**
     * 默认分值
     */
    private BigDecimal score;

    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 默认金额
     */
    private BigDecimal defaultMoney;


    /**
     * 映射/关联结果
     */
    private String mappingResult;

    /**
     * 强制拍照,0不强制1强制
     */
    private Integer mustPic;

    private Long metaColumnId;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 最高分
     */
    @ApiModelProperty("最高分")
    private BigDecimal maxScore;

    /**
     * 最低分
     */
    @ApiModelProperty("最低分")
    private BigDecimal minScore;

    @ApiModelProperty("AI评分区间最小分值")
    private BigDecimal aiMinScore;

    @ApiModelProperty("AI评分区间最大分值")
    private BigDecimal aiMaxScore;

    @ApiModelProperty("分值加倍 0:不加倍，1:加倍")
    private Integer scoreIsDouble;

    @ApiModelProperty("奖罚加倍 0:不加倍，1:加倍")
    private Integer awardIsDouble;

    private static final long serialVersionUID = 1L;

    /**
     * 检查表ID
     */
    @ApiModelProperty("检查表id")
    private Long metaTableId;

    public String convertToExtendInfo() {
        if (Objects.isNull(this.aiMinScore) && Objects.isNull(this.aiMaxScore)) return null;
        JSONObject extendInfo = new JSONObject();
        extendInfo.put("aiMinScore", this.aiMinScore);
        extendInfo.put("aiMaxScore", this.aiMaxScore);
        return extendInfo.toJSONString();
    }
}