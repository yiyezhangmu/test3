package com.coolcollege.intelligent.model.metatable.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @author   wxp
 * @date   2023-08-14 07:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaQuickColumnAppealRequest implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("快速检查项ID")
    private Long metaQuickColumnId;

    @ApiModelProperty("自定义申诉选项名称")
    private String appealName;
}