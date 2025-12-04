package com.coolcollege.intelligent.model.homeTemplate.VO;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.system.VO.SysRoleBaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/6/24 10:08
 * @Version 1.0
 */
@Data
public class HomeTemplateVO {
    /**
     *主键
     */
    @ApiModelProperty("主键")
    private Integer id;
    /**
     *首页模板名称
     */
    @ApiModelProperty("模板名称")
    private String templateName;
    /**
     *首页模板描述
     */
    @ApiModelProperty("模板描述")
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
    @ApiModelProperty("pc组件JSON")
    private JSONObject pcComponentsJson;
    /**
     *APP组件json
     */
    @ApiModelProperty("移动端组件JSON")
    private JSONObject appComponentsJson;
    /**
     *创建人
     */
    @ApiModelProperty("创建人")
    private String createId;
    /**
     *创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     *更新人
     */
    @ApiModelProperty("更新人")
    private String updateId;
    /**
     *更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 角色列表
     */
    @ApiModelProperty("角色列表")
    private List<SysRoleBaseVO> sysRoleBaseVOS;

    /**
     * 查询类型，通过key 或者 ID方式  key是没有发布随机生成的虚拟ID
     */
    @ApiModelProperty("查询类型 key OR id")
    private String checkType;

    /**
     * 角色当前使用的模板
     */
    @ApiModelProperty("当前角色使用的模板状态")
    private Boolean currentHomeTemplateStatus;
}
