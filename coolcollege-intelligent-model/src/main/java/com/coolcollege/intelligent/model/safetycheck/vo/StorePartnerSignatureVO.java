package com.coolcollege.intelligent.model.safetycheck.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author wxp
 * @Date 2023/8/22 18:02
 * @Version 1.0
 */
@Data
public class StorePartnerSignatureVO {

    /**
     * 门店伙伴签字
     */
    @ApiModelProperty("门店伙伴签字url")
    private String signatureUrl;

    /**
     * 签字结果 pass同意 reject拒绝
     */
    @ApiModelProperty("签字结果 pass同意 reject拒绝")
    private String signatureResult;

    /**
     * 签字备注
     */
    @ApiModelProperty("签字备注")
    private String signatureRemark;

    /**
     * 签字人id
     */
    @ApiModelProperty("签字人userId")
    private String signatureUserId;

    /**
     * 签字人信息
     */
    @ApiModelProperty("签字人姓名")
    private String signatureUserName;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("工号")
    private String jobnumber;

    @ApiModelProperty("签字人电话 ")
    private String mobile;
    /**
     * 签字时间
     */
    @ApiModelProperty("签字时间 ")
    private Date signatureTime;
    /**
     * 选择的签字人
     */
    @ApiModelProperty("选择的签字人")
    private String selectSignatureUser;

}
