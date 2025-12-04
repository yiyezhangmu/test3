package com.coolcollege.intelligent.model.login;

import com.coolcollege.intelligent.model.enums.LoginTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Admin
 * @date 2021-07-16 10:39
 */
@Data
public class UserLoginDTO {

    @NotBlank
    private String mobile;

    private String password;

    @NotNull(message = "登录类型不能为空")
    private LoginTypeEnum loginType;

    @ApiModelProperty("切换后的账号id")
    private String userId;

    private String smsCode;

    private String loginWay;

    /**
     * 指定企业登录
     */
    private String enterpriseId;

    @Override
    public String toString() {
        return "UserLoginDTO{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", loginType=" + loginType +
                ", smsCode='" + smsCode + '\'' +
                '}';
    }
}
