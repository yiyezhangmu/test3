package com.coolcollege.intelligent.model.picture.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureCenterColumnVO {
    /**
     * id
     */
    private Long id;

    /**
     * 业务id
     */
    private Long businessId;

    /**
     * 业务表id
     */
    private Long dataTableId;


    /**
     * 检查项名称
     */
    private String metaColumnName;

    /**
     * 检查项Id
     */
    private Long metaColumnId;

    /**
     * 图片路径
     */
    private String pictureUrl;

    /**
     * 图片路径
     */
    private String videoUrl;

    /**
     * 图片路径
     */
    private List<String> displayPictureUrls;

    /**
     * 场景名称
     */
    private String storeSceneName;

    private String supervisionName;

    private String checkText;
    /**
     * 检查项结果:failed,unapplicable,pass
     */
    private String checkResult;

    /**
     * 检查项结果id
     */
    private Long checkResultId;

    /**
     * 检查项结果名称
     */
    private String checkResultName;


    /**
     * 检查项的分值
     */
    private BigDecimal checkScore;

    /**
     * ai分析结果
     */
    private List<PictureCenterColumnAIResultVO> aiResultVOS;

    /**
     * 不合格原因
     */
    @ApiModelProperty("不合格原因")
    private String checkResultReason;


    /**
     * 检查项总分 根据不适用配置计算得出
     */
    @ApiModelProperty("检查项总分 根据不适用配置计算得出")
    private BigDecimal columnMaxScore;
}
