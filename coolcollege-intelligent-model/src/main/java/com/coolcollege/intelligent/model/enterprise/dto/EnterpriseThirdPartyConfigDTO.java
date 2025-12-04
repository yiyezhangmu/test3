package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 企业第三方配置表
 * @author ：xugangkun
 * @date ：2021/8/20 14:27
 */
@Data
public class EnterpriseThirdPartyConfigDTO {
    /**
     * 企业Id
     */
    @NotBlank(message = "corpId不能为空")
    private String corpId;
    /**
     * 企业微信通讯录凭证密钥
     */
    @NotBlank(message = "凭证密钥不能为空")
    private String corpSecret;
}
