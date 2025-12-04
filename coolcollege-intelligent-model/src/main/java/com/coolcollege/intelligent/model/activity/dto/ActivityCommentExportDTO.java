package com.coolcollege.intelligent.model.activity.dto;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/7/10 13:44
 * @Version 1.0
 */
@Data
public class ActivityCommentExportDTO extends ExportBaseRequest {

    private Long activityId;

    private CurrentUser user;

}
