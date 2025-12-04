package com.coolcollege.intelligent.model.enterprise.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: EnterpriseSettingInfoVO
 * @Description: \
 * @date 2023-10-18 14:28
 */
@Data
public class EnterpriseSettingInfoVO {

    @ApiModelProperty("是否发送待办")
    private Boolean sendUpcoming;

    @ApiModelProperty("是否开通业培一体")
    private Boolean accessCoolCollege;

    @ApiModelProperty("是否开启客流数据同步")
    private Boolean syncPassenger;

    @ApiModelProperty("是否开启外部用户")
    private Boolean enableExternalUser;

    @ApiModelProperty("自定义套餐结束时间")
    private Long customizePackageEndTime;

    @ApiModelProperty("ai算法")
    private String aiAlgorithms;

    @ApiModelProperty("扩展字段")
    private String extendField;

}
