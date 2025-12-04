package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/7/8 9:49
 */
@Data
public class ColumnStatisticsRequest extends FileExportBaseRequest {
    /**
     * 区域Id
     */
    private List<Long> regionIds;

    /**
     * 门店id
     */
    private List<String> storeIds;

    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 检查表id
     */
    private Long metaTableId;


    private String dbName;

    private Integer pageSize;

    private Integer pageNum;

}
