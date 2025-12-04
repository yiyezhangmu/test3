package com.coolcollege.intelligent.model.export.request;

import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 用户信息到处
 * @author ：xugangkun
 * @date ：2021/7/23 10:36
 */
@Data
public class UserInfoExportRequest extends FileExportBaseRequest {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 当前登陆用户
     */
    private CurrentUser user;

    @JsonProperty("dept_id")
    private String deptId;

    @JsonProperty("user_status")
    private Integer userStatus;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("region_id")
    private String regionId;

    @JsonProperty("role_id")
    private Long roleId;

    @JsonProperty("job_number")
    private String jobNumber;

    private String mobile;

    private String orderBy;

    private String orderRule;

    private Integer userType;

}
