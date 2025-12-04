package com.coolcollege.intelligent.model.device.request;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/12/15 15:19
 * @Version 1.0
 */
@Data
public class ExportDeviceRequest extends ExportBaseRequest {

    private DeviceListRequest request;

    private CurrentUser user;
}
