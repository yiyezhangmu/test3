package com.coolcollege.intelligent.model.supervision.request;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/7 14:09
 * @Version 1.0
 */
@Data
public class SupervisionDataDetailListExportRequest   extends ExportBaseRequest {

    private CurrentUser user;

    private String  tbMetaTableId;

    private List<Long> parentIds;

    private Long startTimeDate;

    private Long endTimeDate;

    private String type;
    
}
