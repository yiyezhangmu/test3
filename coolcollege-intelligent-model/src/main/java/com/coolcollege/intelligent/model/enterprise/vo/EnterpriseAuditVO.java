package com.coolcollege.intelligent.model.enterprise.vo;

import lombok.Data;

import java.util.Date;

/**
 * 企业审核信息
 * @author ：xugangkun
 * @date ：2021/7/20 10:06
 */
@Data
public class EnterpriseAuditVO {
    /**
     * id
     */
    private Long id;
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 企业名称
     */
    private String enterpriseName;
    /**
     * 申请用户名
     */
    private String applyUserName;
    /**
     * 申请用户手机号
     */
    private String mobile;
    /**
     * 申请日期
     */
    private Date createTime;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 审核状态 0待审核 1审核通过 2审核不通过
     */
    private Integer auditStatus;
    /**
     * 邮箱
     */
    private String remark;
}
