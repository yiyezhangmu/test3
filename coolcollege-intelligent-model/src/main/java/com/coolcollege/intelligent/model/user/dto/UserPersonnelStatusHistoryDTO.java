package com.coolcollege.intelligent.model.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/3 11:06
 */
@Data
public class UserPersonnelStatusHistoryDTO {
    /**
     * 状态名称
     */
    @NotEmpty(message = "状态名称不能为空")
    private String statusName;
    /**
     * 用户id
     */
    @NotEmpty(message = "用户id不能为空")
    private String userId;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 状态有效时间,以天为最小单位
     */
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
}
