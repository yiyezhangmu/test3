package com.coolcollege.intelligent.model.metatable.vo;

import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnReasonDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnReasonDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: TbMetaQuickColumnResultDTO
 * @Description: 快速检查项结果项
 * @date 2022-04-08 10:26
 */
@Data
@Builder
public class TbMetaQuickColumnResultVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("快速检查项ID")
    private Long metaQuickColumnId;

    @ApiModelProperty("自定义名称")
    private String resultName;

    @ApiModelProperty("最大分值")
    private BigDecimal maxScore;

    @ApiModelProperty("最小分值")
    private BigDecimal minScore;

    @ApiModelProperty("分值")
    private BigDecimal score;

    @ApiModelProperty("默认金额")
    private BigDecimal money;

    @ApiModelProperty("映射/关联结果")
    private String mappingResult;

    @ApiModelProperty("强制拍照,0不强制1强制")
    private Integer mustPic;

    @ApiModelProperty("排序")
    private Integer orderNum;

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("分值加倍 0:不加倍，1:加倍")
    private Integer scoreIsDouble;

    @ApiModelProperty("奖罚加倍 0:不加倍，1:加倍")
    private Integer awardIsDouble;

    @ApiModelProperty("AI评分区间最小分值")
    private BigDecimal aiMinScore;

    @ApiModelProperty("AI评分区间最大分值")
    private BigDecimal aiMaxScore;
}
