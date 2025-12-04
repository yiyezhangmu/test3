package com.coolcollege.intelligent.model.metatable.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;

/**
 * @author byd
 */
@Data
public class UpdateSopNodeRequest {
    @ApiModelProperty("分组节点名称")
    @Max(20)
    private String nodeName;

    @ApiModelProperty("上级节点")
    private Long id;

    @ApiModelProperty("共同编辑人 [{type:person,value:}{type:position,value:}]")
    private String commonEditPersonInfo;

    /**
     * 原始选取的使用人[{type:person,value:}{type:position,value:}]
     */
    @ApiModelProperty("原始选取的使用人[{type:person,value:}{type:position,value:}]")
    private String usePersonInfo;

    /**
     * 使用人范围：self-仅自己，all-全部人员，part-部分人员
     */
    @ApiModelProperty("使用人范围：self-仅自己，all-全部人员，part-部分人员")
    private String useRange;


    /**
     * 选取的结果查看人[{type:person,value:}{type:position,value:}]
     */
    @ApiModelProperty("选取的结果查看人[{type:person,value:}{type:position,value:}]")
    private String resultViewPersonInfo;

    /**
     * 结果可见范围：self-仅自己，all-全部人员，part-部分人员
     */
    @ApiModelProperty("结果可见范围：self-仅自己，all-全部人员，part-部分人员")
    private String resultViewRange;

}
