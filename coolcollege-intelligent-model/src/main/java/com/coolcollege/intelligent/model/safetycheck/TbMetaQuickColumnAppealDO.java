package com.coolcollege.intelligent.model.safetycheck;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2023-08-14 07:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaQuickColumnAppealDO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("快速检查项ID")
    private Long metaQuickColumnId;

    @ApiModelProperty("自定义申诉选项名称")
    private String appealName;

    @ApiModelProperty("是否删除:0:未删除，1.删除")
    private Boolean deleted;

    @ApiModelProperty("")
    private String createUserId;

    @ApiModelProperty("")
    private String updateUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}