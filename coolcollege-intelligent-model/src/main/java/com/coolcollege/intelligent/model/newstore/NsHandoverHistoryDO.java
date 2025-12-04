package com.coolcollege.intelligent.model.newstore;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 新店转交记录
 * @author   zhangnan
 * @date   2022-03-04 04:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NsHandoverHistoryDO implements Serializable {
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

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人")
    private String createUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;
}