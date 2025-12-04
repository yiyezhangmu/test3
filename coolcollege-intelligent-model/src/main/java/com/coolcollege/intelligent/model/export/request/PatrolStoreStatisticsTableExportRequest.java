package com.coolcollege.intelligent.model.export.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @Description: 检查表报表详情
 * @Author chenyupeng
 * @Date 2021/7/13
 * @Version 1.0
 */
@Data
public class PatrolStoreStatisticsTableExportRequest extends FileExportBaseRequest{
    /**
     * 区域id列表  选择区域树查询时使用
     */
    private List<String> regionIds;

    /**
     * 门店id集合
     */
    private List<String> storeIds;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 时间范围起始值
     */
    @ApiModelProperty("开始时间 时间戳")
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginDate;
    /**
     * 时间范围截至值
     */
    @ApiModelProperty("结束时间 时间戳")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
    private Date endDate;

    private String dbName;
}
