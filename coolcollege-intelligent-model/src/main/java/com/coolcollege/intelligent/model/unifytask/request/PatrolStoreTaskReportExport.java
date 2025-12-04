package com.coolcollege.intelligent.model.unifytask.request;


import com.coolcollege.intelligent.model.export.request.ExportBaseRequest;
import com.coolcollege.intelligent.model.unifytask.query.TaskReportQuery;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wxp
 * @date 2021/6/23 15:33
 */
@Data
public class PatrolStoreTaskReportExport extends ExportBaseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private TaskReportQuery query;

}
