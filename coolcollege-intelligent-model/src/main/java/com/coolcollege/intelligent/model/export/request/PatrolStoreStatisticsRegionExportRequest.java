package com.coolcollege.intelligent.model.export.request;

import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/6/4 16:51
 */
@Data
public class PatrolStoreStatisticsRegionExportRequest extends FileExportBaseRequest {
    /**
     * 区域id列表  选择区域树查询时使用
     */
    private List<String> regionIds;

    CurrentUser user;

    /**
     * 时间范围起始值
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginDate;
    /**
     * 时间范围截至值
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
    private Date endDate;
}
