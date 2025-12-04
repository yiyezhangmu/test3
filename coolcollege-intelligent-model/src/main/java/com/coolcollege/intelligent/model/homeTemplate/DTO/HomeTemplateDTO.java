package com.coolcollege.intelligent.model.homeTemplate.DTO;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 15:43
 * @Version 1.0
 */
@ApiModel
@Data
public class HomeTemplateDTO {
    /**
     *主键
     */
    @ApiModelProperty("主键")
    private Integer id;
    /**
     *首页模板名称
     */
    @ApiModelProperty("首页模板名称")
    private String templateName;
    /**
     *首页模板描述
     */
    @ApiModelProperty("首页模板描述")
    private String templateDescription;
    /**
     *系统默认标识
     */
    @ApiModelProperty("系统默认标识")
    private Integer isDefault;
    /**
     *是否删除:0:未删除，1.删除
     */
    @ApiModelProperty("是否删除:0:未删除，1.删除")
    private Integer deleted;
    /**
     *PC组件json
     */
    @ApiModelProperty("PC组件json对象")
    private JSONObject pcComponentsJson;
    /**
     *APP组件json
     */
    @ApiModelProperty("APP组件json对象")
    private JSONObject appComponentsJson;

    /**
     * 角色列表
     */
    @ApiModelProperty("角色ID列表")
    private List<Long> roleIds;

}
