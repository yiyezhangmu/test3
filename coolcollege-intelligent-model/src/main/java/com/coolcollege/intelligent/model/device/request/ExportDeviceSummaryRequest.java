package com.coolcollege.intelligent.model.device.request;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/12/16 15:32
 * @Version 1.0
 */
@Data
public class ExportDeviceSummaryRequest extends ExportBaseRequest {

    private DeviceReportSearchRequest request;

    private CurrentUser user;
}
