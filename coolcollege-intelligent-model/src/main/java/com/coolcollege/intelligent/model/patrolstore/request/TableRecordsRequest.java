package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.export.request.ExportBaseRequest;
import lombok.Data;

import java.util.Date;

/**
 * @author byd
 */
@Data
public class TableRecordsRequest extends ExportBaseRequest {
    private Date beginDate;
    private Date endDate;
    private String regionId;
    private Boolean isComplete = Boolean.FALSE;
    private String regionPath;
    /**
     * 标准检查表id
     */
    private Long metaTableId;

    /**
     * 巡店人id
     */
    private String supervisorId;

    /**
     * 状态
     */
    private Integer status;
}
