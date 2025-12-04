package com.coolcollege.intelligent.service.requestBody.store;

import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 门店覆盖
 * @Author chenyupeng
 * @Date 2021/7/13
 * @Version 1.0
 */
@Data
public class StoreGroupExportRequest extends FileExportBaseRequest {
    /**
     * 分组id
     */
    private String groupId;

    @ApiModelProperty(hidden = true)
    private CurrentUser currentUser;
}
