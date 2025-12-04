package com.coolcollege.intelligent.model.metatable;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2022-04-01 08:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaQuickColumnResultDO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("快速检查项ID")
    private Long metaQuickColumnId;

    @ApiModelProperty("自定义名称")
    private String resultName;

    @ApiModelProperty("最高分")
    private BigDecimal maxScore;

    @ApiModelProperty("最低分")
    private BigDecimal minScore;

    @ApiModelProperty("分值")
    private BigDecimal score;

    @ApiModelProperty("默认金额")
    private BigDecimal defaultMoney;

    @ApiModelProperty("统计维度 映射/关联结果")
    private String mappingResult;

    @ApiModelProperty("检查图片,0不强制1强制")
    private Integer mustPic;

    @ApiModelProperty("排序")
    private Integer orderNum;

    @ApiModelProperty("检查描述")
    private String description;

    @ApiModelProperty("分值加倍 0:不加倍，1:加倍")
    private Integer scoreIsDouble;

    @ApiModelProperty("奖罚加倍 0:不加倍，1:加倍")
    private Integer awardIsDouble;

    @ApiModelProperty("是否删除:0:未删除，1.删除")
    private Integer deleted;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("创建者")
    private String updateUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("扩展信息")
    private String extendInfo;
}