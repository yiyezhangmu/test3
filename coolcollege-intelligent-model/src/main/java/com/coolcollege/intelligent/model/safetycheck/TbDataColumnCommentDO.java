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
 * @author   zhangchenbiao
 * @date   2023-08-14 07:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDataColumnCommentDO implements Serializable {
    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("巡店记录id")
    private Long businessId;

    @ApiModelProperty("操作历史id")
    private Long historyId;

    @ApiModelProperty("操作人id")
    private String operateUserId;

    @ApiModelProperty("操作人姓名")
    private String operateUserName;

    @ApiModelProperty("数据表的ID")
    private Long dataTableId;

    @ApiModelProperty("表ID")
    private Long metaTableId;

    @ApiModelProperty("检查项id")
    private Long metaColumnId;

    @ApiModelProperty("数据检查项id")
    private Long dataColumnId;

    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    @ApiModelProperty("点评结果,下拉选择")
    private String commentResult;

    @ApiModelProperty("点评备注")
    private String commentRemark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("门店id")
    private String storeId;
}