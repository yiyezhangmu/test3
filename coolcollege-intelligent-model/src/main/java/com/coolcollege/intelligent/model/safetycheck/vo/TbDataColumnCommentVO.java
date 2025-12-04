package com.coolcollege.intelligent.model.safetycheck.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-08-14 07:53
 */
@ApiModel
@Data
public class TbDataColumnCommentVO {

    @ApiModelProperty("操作人姓名")
    private String operateUserName;

    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    @ApiModelProperty("点评结果,下拉选择")
    private String commentResult;

    @ApiModelProperty("点评备注")
    private String commentRemark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("是否有点评历史")
    private Boolean hasCommentHistory;

}