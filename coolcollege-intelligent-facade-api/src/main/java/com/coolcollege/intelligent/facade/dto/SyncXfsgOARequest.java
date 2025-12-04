package com.coolcollege.intelligent.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据同步
 *
 * @author wxp
 * @since 2021/9/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncXfsgOARequest {

    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 用户userId
     */
    String userId;
    /**
     * 工号
     */
    private String staffNumber;
    /**
     * 身份证
     */
    private String idCard;
    /**
     * 部门code
     */
    private String departmentCode;
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 岗位Id
     */
    private String jobId;
    /**
     * 岗位名称
     */
    private String jobName;
    /**
     * 员工状态：0离职、1转正、2试用、3实习、4待离职、5临时工、6暑假工、7兼职工
     */
    private Integer status;

}
