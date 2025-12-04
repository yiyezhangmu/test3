package com.coolcollege.intelligent.model.export.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhangnan
 * @date 2022-03-09 16:37
 */
@Data
public class NsStoreListExportRequest extends DynamicFieldsExportRequest{

    @ApiModelProperty("开始创建时间")
    private Long createTimeStart;

    @ApiModelProperty("结束创建时间")
    private Long createTimeEnd;

    @NotNull(message = "请选择区域")
    @ApiModelProperty("区域id")
    private Long regionId;

    @ApiModelProperty("门店名称")
    private String name;

    @ApiModelProperty("门店类型")
    private List<String> typeList;

    @ApiModelProperty("新店状态：ongoing(进行中),completed(完成),failed(失败)")
    private List<String> statusList;

    @ApiModelProperty("负责人")
    private String directUserId;

    private String createUserId;

}
