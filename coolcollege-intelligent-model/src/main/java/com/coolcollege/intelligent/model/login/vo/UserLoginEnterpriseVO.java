package com.coolcollege.intelligent.model.login.vo;

import com.coolstore.base.enums.AppTypeEnum;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: UserLoginEnterpriseVO
 * @Description: 用户登录企业信息
 * @date 2021-07-16 11:31
 */
@Data
public class UserLoginEnterpriseVO {

    private String enterpriseLogo;

    private String enterpriseId;

    private String enterpriseName;

    private String originalName;

    private Integer isVip;

    private String dingCropId;

    private Date createTime;

    /**
     * 来源类型
     */
    private String appType;

    /**
     * 来源类型名称
     */
    private String appTypeName;

    public UserLoginEnterpriseVO(String enterpriseLogo, String enterpriseId, String enterpriseName, String originalName, Integer isVip, String dingCropId) {
        this.enterpriseLogo = enterpriseLogo;
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.originalName = originalName;
        this.isVip = isVip;
        this.dingCropId = dingCropId;
    }

    public UserLoginEnterpriseVO(String enterpriseLogo, String enterpriseId, String enterpriseName, String originalName, Integer isVip, String dingCropId, Date createTime, String appType) {
        this.enterpriseLogo = enterpriseLogo;
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.originalName = originalName;
        this.isVip = isVip;
        this.dingCropId = dingCropId;
        this.createTime = createTime;
        this.appType = appType;
    }

    public String getAppTypeName() {
        return AppTypeEnum.getMessage(this.appType);
    }
}
