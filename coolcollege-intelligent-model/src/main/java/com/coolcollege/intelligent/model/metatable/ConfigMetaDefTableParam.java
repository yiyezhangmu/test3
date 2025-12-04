package com.coolcollege.intelligent.model.metatable;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yezhe
 * @date 2020-12-12 15:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigMetaDefTableParam {
    /**
     * ID
     */
    private Long tableId;

    /**
     * 检查表名称
     */
    private String tableName;

    /**
     * 描述信息
     */
    @ApiModelProperty("自定义描述信息")
    private String description;

    /**
     * 权限范围_人员ID
     */
    private String shareGroup;

    /**
     * 权限范围_职位ID
     */
    private String shareGroupPosition;

    /**
     * 检查项schemaJson数据
     */
    private String properties;

    /**
     * 结果可见权限_人员ID
     */
    private String resultShareGroup;

    /**
     * 结果可见权限_职位ID
     */
    private String resultShareGroupPosition;

    /**
     * 检查表类型
     */
    private String tableType;

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
    @ApiModelProperty("原始选取的使用人[{type:person,value:}{type:position,value:}]")
    private String resultViewPersonInfo;

    /**
     * 结果可见范围：self-仅自己，all-全部人员，part-部分人员
     */
    @ApiModelProperty("结果可见范围：self-仅自己，all-全部人员，part-部分人员")
    private String resultViewRange;

    @ApiModelProperty("共同编辑人userId集合")
    private List<String> commonEditUserIdList;

    /**
     * 共同编辑人userId集合（前后逗号分隔）
     */
    @ApiModelProperty("结果查看人是否包含使用人")
    private Boolean resultViewUserWithUserRang;

    @ApiModelProperty("自定义表分组")
    private String pid;
}
