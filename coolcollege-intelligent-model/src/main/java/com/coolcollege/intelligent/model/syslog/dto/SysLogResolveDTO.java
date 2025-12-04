package com.coolcollege.intelligent.model.syslog.dto;

import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Optional;

/**
 * describe: 系统日志操作消息DTO
 *
 * @author wangff
 * @date 2025/1/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysLogResolveDTO {
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 菜单
     */
    private String menus;
    /**
     * 功能
     */
    private String func;
    /**
     * 子功能
     */
    private String subFunc;
    /**
     * 操作人id
     */
    private String opUserId;
    /**
     * 操作人名称
     */
    private String opUserName;
    /**
     * 操作人手机号
     */
    private String opUserMobile;
    /**
     * 操作人工号
     */
    private String opUserJobnumber;
    /**
     * 操作时间
     */
    private Date opTime;
    /**
     * 登录ip
     */
    private String opIp;
    /**
     * 设备信息
     */
    private String deviceInfo;
    /**
     * 请求参数
     */
    private String reqParams;
    /**
     * 响应参数
     */
    private String respParams;
    /**
     * 请求路径
     */
    private String url;
    /**
     * 扩展字段
     */
    private String extendInfo;
    /**
     * 模块
     */
    OpModuleEnum opModule;
    /**
     * 操作类型
     */
    OpTypeEnum opType;
    /**
     * 是否处理操作内容
     */
    boolean resolve;

    public SysLogDO convertToSysLogDO() {
        return SysLogDO.builder()
                .menus(this.menus)
                .module(Optional.ofNullable(this.menus).map(v -> v.split("-")).map(v -> v[v.length - 1]).orElse(""))
                .func(this.func)
                .subFunc(this.subFunc)
                .opUserId(this.opUserId)
                .opUserName(this.opUserName)
                .opUserMobile(this.opUserMobile)
                .opUserJobnumber(this.opUserJobnumber)
                .opTime(this.opTime)
                .opType(this.opType.getType())
                .opIp(this.opIp)
                .deviceInfo(this.deviceInfo)
                .reqParams(this.reqParams)
                .respParams(this.respParams)
                .url(this.url)
                .extendInfo(this.extendInfo)
                .build();
    }
}
