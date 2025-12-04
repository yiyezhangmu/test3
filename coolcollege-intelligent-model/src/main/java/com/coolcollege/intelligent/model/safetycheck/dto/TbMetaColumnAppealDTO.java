package com.coolcollege.intelligent.model.safetycheck.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   wxp
 * @date   2023-08-14 07:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaColumnAppealDTO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("检查表ID")
    private Long metaTableId;

    @ApiModelProperty("检查项ID")
    private Long metaColumnId;

    @ApiModelProperty("自定义申诉选项名称")
    private String appealName;

}