package com.coolcollege.intelligent.model.login;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 鱼你登录
 * @date 2021-07-16 10:39
 */
@Data
public class YNUserLoginDTO {

    @NotBlank(message = "企业id不能为空")
    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("应用ID")
    private String appId;

    @ApiModelProperty("签名方式,默认值:HMAC-SHA256")
    private String signType;

    @ApiModelProperty("请求唯— ID， 最大长度32位")
    private String requestId;

    @ApiModelProperty("当前请求的时间戳【单位为毫秒，位数为13】 ,注意该 时间戳应该根据当前时间实时生成,有效时间时长为10分钟")
    private Long timestamp;

    @NotBlank(message = "签名不能为空")
    @ApiModelProperty("签名,按照签名规则生成签名值")
    private String sign;

    @NotNull(message = "用户类型不能为空")
    @Min(1)@Max(2)
    @ApiModelProperty("用户类型 1手机号 2员工号")
    private Integer userType;

    @NotBlank(message = "userName不能为空")
    @ApiModelProperty("userType 对应的值")
    private String userName;

}
