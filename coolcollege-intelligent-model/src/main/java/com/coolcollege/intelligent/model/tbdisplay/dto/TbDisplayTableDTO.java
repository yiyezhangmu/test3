package com.coolcollege.intelligent.model.tbdisplay.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author byd
 */
@ApiModel
@Data
public class TbDisplayTableDTO {

    private Long id;

    /**
     * 检查表名称
     */
    private String name;

    /**
     * 范围
     */
    private String scope;
    /**
     * 范围类型 all全部 part 部分
     */
    private String scopeType;
    /**
     * 范围id
     */
    private String scopeId;

    /**
     * 删除 0 未删除 1已删除
     */
    private Integer deleted;


    /**
     * 检查项列表
     */
    List<TbDisplayTableItemDTO> tableItemList;

    /**
     * 检查内容列表
     */
    List<TbDisplayTableItemDTO> tableContentList;

    /**
     * 是否是高级检查表
     */
    private Integer tableProperty;

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
     * 共同编辑人userId集合（前后逗号分隔）
     */
    @ApiModelProperty("共同编辑人userId集合（前后逗号分隔）")
    private String commonEditUserids;

    @ApiModelProperty("共同编辑人userId集合")
    private List<String> commonEditUserIdList;

}
