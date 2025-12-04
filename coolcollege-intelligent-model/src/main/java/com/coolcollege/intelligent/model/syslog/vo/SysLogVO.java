package com.coolcollege.intelligent.model.syslog.vo;

import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Date;

/**
 * describe: 系统日志VO对象
 *
 * @author wangff
 * @date 2025/2/7
 */
@ApiModel("系统日志")
@Builder
@Data
public class SysLogVO {
    @ApiModelProperty("模块")
    private String module;

    @ApiModelProperty("操作人")
    private String opUser;

    @ApiModelProperty("操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date opTime;

    @ApiModelProperty("操作类型")
    private String opType;

    @ApiModelProperty("操作内容")
    private String opContent;

    @ApiModelProperty("登录ip")
    private String opIp;

    @ApiModelProperty("设备信息")
    private String deviceInfo;

    public static SysLogVO convert(SysLogDO sysLogDO) {
        return SysLogVO.builder()
                .module(sysLogDO.getModule())
                .opUser(sysLogDO.getOpUserName() + (StringUtils.isNotBlank(sysLogDO.getOpUserJobnumber()) ? "(" + sysLogDO.getOpUserJobnumber() + ")" : ""))
                .opTime(sysLogDO.getOpTime())
                .opType(sysLogDO.getOpType())
                .opContent(sysLogDO.getOpContent())
                .opIp(sysLogDO.getOpIp())
                .deviceInfo(sysLogDO.getDeviceInfo())
                .build();
    }
}
