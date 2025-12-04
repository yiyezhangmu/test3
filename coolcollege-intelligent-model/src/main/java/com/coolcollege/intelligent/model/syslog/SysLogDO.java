package com.coolcollege.intelligent.model.syslog;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统日志
 * @author   wangff
 * @date   2025-01-20 01:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysLogDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("菜单")
    private String menus;

    @ApiModelProperty("模块")
    private String module;

    @ApiModelProperty("功能")
    private String func;

    @ApiModelProperty("子功能")
    private String subFunc;

    @ApiModelProperty("操作人id")
    private String opUserId;

    @ApiModelProperty("操作人名称")
    private String opUserName;

    @ApiModelProperty("操作人手机号")
    private String opUserMobile;

    @ApiModelProperty("操作人工号")
    private String opUserJobnumber;

    @ApiModelProperty("操作时间")
    private Date opTime;

    @ApiModelProperty("操作类型")
    private String opType;

    @ApiModelProperty("操作内容")
    private String opContent;

    @ApiModelProperty("登录ip")
    private String opIp;

    @ApiModelProperty("设备信息")
    private String deviceInfo;

    @ApiModelProperty("请求参数")
    private String reqParams;

    @ApiModelProperty("响应参数")
    private String respParams;

    @ApiModelProperty("请求路径")
    private String url;

    @ApiModelProperty("扩展字段")
    private String extendInfo;

    /**
     * 标记为删除的不添加到表中
     */
    private Boolean delete;

    public void setModuleByMenus() {
        this.module = Optional.ofNullable(this.menus).map(v -> v.split("-")).map(v -> v[v.length - 1]).orElse("");
    }
}