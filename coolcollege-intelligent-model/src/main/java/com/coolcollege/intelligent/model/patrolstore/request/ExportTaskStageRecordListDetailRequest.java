package com.coolcollege.intelligent.model.patrolstore.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author byd
 */
@Data
public class ExportTaskStageRecordListDetailRequest extends ExportBaseRequest implements Serializable {


    private static final long serialVersionUID = 1L;

    private Long businessId;

}
