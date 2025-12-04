package com.coolcollege.intelligent.model.patrolstore.query;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.coolcollege.intelligent.model.export.request.ExportBaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基础查询参数
 * 
 * @author jeffrey
 * @date 2020/12/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsBaseQuery extends ExportBaseRequest {
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
     * 页码
     */
    private Integer pageNum = 1;
    /**
     * 每页大小
     */
    private Integer pageSize = 500;

}
