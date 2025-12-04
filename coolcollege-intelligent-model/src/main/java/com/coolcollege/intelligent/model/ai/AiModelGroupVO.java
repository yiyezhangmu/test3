package com.coolcollege.intelligent.model.ai;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * AI算法分组
 * @author   zhangchenbiao
 * @date   2025-09-25 03:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiModelGroupVO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("备注")
    private String remark;

}