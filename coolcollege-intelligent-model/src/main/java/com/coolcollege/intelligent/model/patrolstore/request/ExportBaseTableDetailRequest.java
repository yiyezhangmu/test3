package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataStaColumnQuery;
import lombok.Data;

import java.io.Serializable;

/**
 * @author byd
 */
@Data
public class ExportBaseTableDetailRequest extends ExportBaseRequest implements Serializable {


    private static final long serialVersionUID = 1L;

    PatrolStoreStatisticsDataStaColumnQuery request;

}
