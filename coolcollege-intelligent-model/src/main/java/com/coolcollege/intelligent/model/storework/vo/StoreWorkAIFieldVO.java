package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 店务AI字段VO
 * </p>
 *
 * @author wangff
 * @since 2025/6/16
 */
@Data
public class StoreWorkAIFieldVO {
    @ApiModelProperty("是否需要执行AI")
    private Integer isAiProcess;

    @ApiModelProperty("AI执行状态，0未执行 &1已点评 &2点评人已点评")
    private Integer aiStatus;

    @ApiModelProperty("前端用于判断AI执行状态，受AI结果处理方式影响，0未点评 1AI已点评 2已点评 3已复核 4AI点评失败")
    private Integer aiStatusDisplayFlag;
}
