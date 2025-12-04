package com.coolcollege.intelligent.model.supervision.request;

import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/3 16:29
 * @Version 1.0
 */
@Data
public class SupervisionTaskDataListExportRequest extends ExportBaseRequest {

    private Long parentId;

    private List<String> userIds;

    private List<String> storeIds;

    private List<String> regionId;

    private List<SupervisionSubTaskStatusEnum> completeStatusList;

    private CurrentUser user;

    private String userName;

    private Integer handleOverTimeStatus;

    private Long taskId;

}
