package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 审核企业
 * @author ：xugangkun
 * @date ：2021/7/20 15:35
 */
@Data
public class EnterpriseAuditDTO {
    /**
     * 申请记录id
     */
    @NotNull(message = "id不能为空")
    private Long id;
    /**
     * 审核状态 0待审核 1审核通过 2审核不通过
     */
    @NotNull(message = "审核状态不能为空")
    private Integer auditStatus;
    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空")
    private String remark;

}
