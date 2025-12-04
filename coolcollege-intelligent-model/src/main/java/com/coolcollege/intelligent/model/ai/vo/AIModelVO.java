package com.coolcollege.intelligent.model.ai.vo;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * AI模型VO
 * </p>
 *
 * @author wangff
 * @since 2025/6/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIModelVO {
    @ApiModelProperty("编码")
    private String code;

    @ApiModelProperty("展示名称")
    private String showName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否支持编辑")
    private Boolean supportCustomPrompt;

    public static AIModelVO convert(AiModelLibraryDO aiModelLibraryDO) {
        return new AIModelVO(aiModelLibraryDO.getCode(), aiModelLibraryDO.getName(), aiModelLibraryDO.getRemark(), aiModelLibraryDO.isSupportCustomPrompt());
    }
}
