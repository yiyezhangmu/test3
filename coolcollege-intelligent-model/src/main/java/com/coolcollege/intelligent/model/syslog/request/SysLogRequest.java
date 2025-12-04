package com.coolcollege.intelligent.model.syslog.request;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * describe: 系统日志查询
 *
 * @author wangff
 * @date 2025/2/7
 */
@ApiModel("系统日志查询")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysLogRequest extends PageBaseRequest {
    @ApiModelProperty("操作内容")
    private String opContent;

    @ApiModelProperty("操作人id")
    private String opUserId;

    @ApiModelProperty("操作人名称")
    private String opUserName;

    @ApiModelProperty("操作人手机号")
    private String opUserMobile;

    @ApiModelProperty("操作人工号")
    private String opUserJobnumber;

    @ApiModelProperty("模块")
    private String module;

    private List<String> modules;

    @ApiModelProperty("操作开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date opTimeStart;

    @ApiModelProperty("操作结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date opTimeEnd;
}
