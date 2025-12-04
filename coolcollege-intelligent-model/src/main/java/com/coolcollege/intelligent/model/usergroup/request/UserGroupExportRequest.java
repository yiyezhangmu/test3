package com.coolcollege.intelligent.model.usergroup.request;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wxp
 * @since 2023/1/4
 */
@Data
public class UserGroupExportRequest extends FileExportBaseRequest {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 当前登陆用户
     */
    private CurrentUser user;
    /**
     * 分组id
     */
    @ApiModelProperty("分组id")
    private String groupId;
}
