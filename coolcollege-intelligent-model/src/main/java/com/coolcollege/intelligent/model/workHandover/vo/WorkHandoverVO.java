package com.coolcollege.intelligent.model.workHandover.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author   zhangchenbiao
 * @date   2022-11-16 11:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkHandoverVO implements Serializable {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("移交人id")
    private String transferUserId;

    @ApiModelProperty("移交人名称")
    private String transferUserName;

    @ApiModelProperty("交接人id")
    private String handoverUserId;

    @ApiModelProperty("交接人名称")
    private String handoverUserName;

    @ApiModelProperty("交接内容")
    private String handoverContent;

    @ApiModelProperty("交接状态 0:交接中 1:交接成功 2:交接失败")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("交接完成时间")
    private Date completeTime;

    @ApiModelProperty("交接内容列表")
    private List<String> handoverContentList;
}