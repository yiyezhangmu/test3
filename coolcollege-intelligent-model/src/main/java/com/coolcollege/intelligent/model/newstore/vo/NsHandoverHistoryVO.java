package com.coolcollege.intelligent.model.newstore.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangnan
 * @description: 新店交接记录VO
 * @date 2022/3/7 8:21 PM
 */
@Data
public class NsHandoverHistoryVO {
    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("交接人")
    private String oldDirectUserId;

    @ApiModelProperty("交接人姓名")
    private String oldDirectUserName;

    @ApiModelProperty("接收人")
    private String newDirectUserId;

    @ApiModelProperty("接收人姓名")
    private String newDirectUserName;

    @ApiModelProperty("新店id")
    private Long newStoreId;

    @ApiModelProperty("新店名称")
    private String newStoreName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人")
    private String createUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;
}
