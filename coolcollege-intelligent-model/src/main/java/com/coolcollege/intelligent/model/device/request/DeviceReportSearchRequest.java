package com.coolcollege.intelligent.model.device.request;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import lombok.Data;

import java.util.List;

@Data
public class DeviceReportSearchRequest extends PageBaseRequest {

    private List<String> regionIds;
    private List<String> storeIds;
    private String keyword;
    private List<String> storeStatus;
    private Integer offlineCount;

}
