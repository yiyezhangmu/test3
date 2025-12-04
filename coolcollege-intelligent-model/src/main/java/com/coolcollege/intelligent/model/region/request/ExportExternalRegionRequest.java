package com.coolcollege.intelligent.model.region.request;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/12/15 15:19
 * @Version 1.0
 */
@Data
public class ExportExternalRegionRequest extends ExportBaseRequest {

    private ExternalRegionExportRequest request;

    private CurrentUser user;
}
