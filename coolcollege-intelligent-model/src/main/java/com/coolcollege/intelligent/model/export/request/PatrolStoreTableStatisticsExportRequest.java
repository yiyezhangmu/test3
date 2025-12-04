package com.coolcollege.intelligent.model.export.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/6/7 17:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreTableStatisticsExportRequest extends FileExportBaseRequest{
    /**
     * 检查表
     */
    private List<Long> metaTableIds;

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

    /**
     * @see
     */
    private String tableType;
}
