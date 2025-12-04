package com.coolcollege.intelligent.model.patrolstore;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/11/18
 */
@Data
public class TbPatrolStoreRecordInfoDO {

    public static final String PARAMS_SIGN_IN_IMG = "signInImg";
    public static final String PARAMS_SIGN_OUT_IMG = "signOutImg";

    /**
     * 巡店记录Id
     */
    private Long id;

    private String eid;

    /**
     *签到方式
     */
    private String signInWay;

    /**
     * 签到备注
     */
    private String signInRemark;

    /**
     *签退方式
     */
    private String signOutWay;

    /**
     *签退备注
     */
    private String signOutRemark;

    private String params;
    

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 审核人id
     */
    private String auditUserId;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核图片
     */
    private String auditPicture;

    /**
     * 审核意见
     */
    private String auditOpinion;

    /**
     * 审核姓名
     */
    private String auditUserName;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 门店伙伴签字
     */
    private String signatureUrl;

    /**
     * 签字结果 pass同意 reject拒绝
     */
    private String signatureResult;

    /**
     * 签字备注
     */
    private String signatureRemark;

    /**
     * 签字人id
     */
    private String signatureUserId;

    /**
     * 签字时间
     */
    private Date signatureTime;

    /**
     * 稽核完成时间
     */
    private Date finishTime;

    /**
     * 巡店人id
     */
    private String supervisorId;

    /**
     * 巡店人姓名
     */
    private String supervisorName;

    private String patrolType;

    /**
     * 巡店结束时间
     */
    private Date signEndTime;

    private Integer auditRejectNum;

    private Integer appealPassNum;

    private Integer appealRejectNum;


    /**
     * 删除人id
     */
    private String deleteUserId;

    /**
     * 删除人名称
     */
    private String deleteUserName;


    /**
     * 删除时间
     */
    private Date deleteTime;

}
