package com.coolcollege.intelligent.model.enterprise;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2022-08-17 07:49
 */
@Data
public class EnterpriseOpenLeaveInfoDO {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("开通人姓名")
    private String name;

    @ApiModelProperty("电话")
    private String mobile;

    @ApiModelProperty("门店数量")
    private Integer storeNum;

    @ApiModelProperty("企业cropId")
    private String corpId;

    @ApiModelProperty("企业名称")
    private String corpName;

    @ApiModelProperty("来源类型：默认dingding钉钉,qw企业微信 mobile")
    private String appType;

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}