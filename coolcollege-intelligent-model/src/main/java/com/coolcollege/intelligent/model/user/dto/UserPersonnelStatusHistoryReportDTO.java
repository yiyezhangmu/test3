package com.coolcollege.intelligent.model.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/4 10:42
 */
@Data
public class UserPersonnelStatusHistoryReportDTO {
    /**
     * 选择的开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    @NotNull(message = "开始时间不能为空")
    private Date startTime;
    /**
     * 选择的结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    /**
     * 选择的用户列表
     */
    private List<UserSimpleDTO> userList;
    /**
     * 选择的角色id列表
     */
    private List<RoleSimpleDTO> roleList;
    /**
     * 状态名称
     */
    private String statusName;

    private Integer pageSize = 10;

    private Integer pageNum = 1;
}
