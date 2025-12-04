package com.coolcollege.intelligent.model.enterprise.dto;


import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class DeviceAuthReportPageDTO extends PageBaseRequest {

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("企业标签")
    private String tag;

    @ApiModelProperty("csm")
    private String csm;

    @ApiModelProperty("查询日期")
    private String queryDate;

    @ApiModelProperty("企业ids")
    private List<String> enterpriseIds;

    public boolean isQueryEnterprise() {
        return !StringUtils.isAllBlank(enterpriseId, enterpriseName, tag, csm);
    }

}
