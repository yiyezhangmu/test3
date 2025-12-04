package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import lombok.Data;

import java.io.Serializable;

/**
 * @author byd
 */
@Data
public class ExportTaskStageRecordListRequest extends ExportBaseRequest implements Serializable {


    private static final long serialVersionUID = 1L;

    PatrolStoreStatisticsDataTableQuery request;

}
