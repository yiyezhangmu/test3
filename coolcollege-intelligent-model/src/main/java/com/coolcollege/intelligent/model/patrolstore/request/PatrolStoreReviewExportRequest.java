package com.coolcollege.intelligent.model.patrolstore.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/8/23 9:59
 */
@Data
public class PatrolStoreReviewExportRequest extends ExportBaseRequest implements Serializable {

    private List<String> recordIds;

}
