package com.coolcollege.intelligent.model.user.dto;

import com.coolcollege.intelligent.model.device.request.DeviceListRequest;
import com.coolcollege.intelligent.model.export.request.UserInfoExportRequest;
import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/12/15 15:19
 * @Version 1.0
 */
@Data
public class ExportUserRequest extends ExportBaseRequest {

    private UserInfoExportRequest request;

    private CurrentUser user;
}
