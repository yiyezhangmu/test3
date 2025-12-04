package com.coolcollege.intelligent.model.patrolstore.param;

import javax.validation.constraints.NotNull;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yezhe
 * @date 2020-12-08 19:25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreSignInParam {
    private Long businessId;

    /**巡店地址不能为空*/
    private String signStartAddress;

    /**巡店经纬度不能为空*/
    private String startLongitudeLatitude;

    @NotNull(message = "巡店打卡状态")
    private Integer signInStatus;

    // 自主巡店相关参数
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 巡店类型
     */
    private String patrolType;
    /**
     * 签到时间,不传则使用当前时间
     */
    private Date signStartTime;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 打卡方式
     */
    private String signInWay;

    /**
     * 打卡备注
     */
    private String signInRemark;


    /**
     * 巡店配置
     */
    private EnterpriseStoreCheckSettingDO storeCheckSetting;

    private String dingCorpId;

    private String appType;

    /**
     * 签到图片
     */
    private String signInImg;

    /**
     * 第三方业务id
     */
    private String thirdBusinessId;
}
