package com.coolcollege.intelligent.model.metatable.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-06-05 02:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaColumnReasonRequest implements Serializable {
    @ApiModelProperty("id, 修改必填")
    private Long id;

    @ApiModelProperty("自定义名称")
    private String reasonName;

    @ApiModelProperty("统计维度:  FAIL:不合格 INAPPLICABLE:不适用")
    private String mappingResult;
}