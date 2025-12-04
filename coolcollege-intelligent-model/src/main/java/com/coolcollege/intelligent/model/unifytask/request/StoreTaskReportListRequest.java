package com.coolcollege.intelligent.model.unifytask.request;

import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.page.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author byd
 */
@Data
public class StoreTaskReportListRequest extends PageRequest {

    @NotNull
    @ApiModelProperty("开始时间")
    private Long beginTime;

    @NotNull
    @ApiModelProperty("结束时间")
    private Long endTime;

    @ApiModelProperty("区域id")
    private List<String> regionIds;

    @ApiModelProperty("门店id")
    private List<String> storeIds;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("巡店人员id列表")
    private List<String> userIdList;

    ExportServiceEnum exportServiceEnum;
}
