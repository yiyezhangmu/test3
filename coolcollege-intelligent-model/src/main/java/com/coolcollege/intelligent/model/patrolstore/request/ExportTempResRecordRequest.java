package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import lombok.Data;

import java.io.Serializable;

/**
 * @author byd
 */
@Data
public class ExportTempResRecordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    PatrolStoreStatisticsDataTableQuery request;

    String enterpriseId;

    ImportTaskDO importTaskDO;

    /**
     * 总数量
     */
    private Long totalNum;

}
