package com.coolcollege.intelligent.model.boss.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：xugangkun
 * @date ：2022/3/31 15:01
 */
@ApiModel("企业标签返回实体")
@Data
public class EnterpriseTagVO {

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("企业标签")
    private String tag;

}
