package com.coolcollege.intelligent.model.region.request;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: ExternalRegionExportRequest
 * @Description:
 * @date 2023-10-24 11:39
 */
@Data
public class ExternalRegionExportRequest extends FileExportBaseRequest {

    private String enterpriseId;

    private CurrentUser user;

}
