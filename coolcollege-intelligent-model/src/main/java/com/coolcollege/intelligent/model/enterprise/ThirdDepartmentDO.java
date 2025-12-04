package com.coolcollege.intelligent.model.enterprise;

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
 * @date   2024-04-02 04:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdDepartmentDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("三方部门code")
    private String departmentCode;

    @ApiModelProperty("三方部门名称")
    private String departmentName;

    @ApiModelProperty("部门负责人 鲜丰水果是工号")
    private String deptPrincipal;

    @ApiModelProperty("父部门code")
    private String parentDepartmentCode;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}