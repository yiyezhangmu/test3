package com.coolcollege.intelligent.model.enterprise;

import lombok.Data;

import java.util.Date;



/**
 * 企业审核表
 * @author xugangkun
 * @date 2021-07-19 16:27:52
 */
@Data
public class EnterpriseAuditInfoDO {
	/**
	* 自增id
	*/
	private Long id;
	/**
	* 企业Id
	*/
	private String enterpriseId;
	/**
	* 企业名称
	*/
	private String enterpriseName;
	/**
	* 审核状态 0待审核 1审核通过 2审核不通过
	*/
	private Integer auditStatus;
	/**
	 * 审核用户id
	 */
	private String auditUserId;
	/**
	 * 申请用户名称
	 */
	private String applyUserName;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	* 审核备注
	*/
	private String remark;
	/**
	* 审核时间
	*/
	private Date auditTime;
	/**
	* 创建时间
	*/
	private Date createTime;
	/**
	* 更新时间
	*/
	private Date updateTime;
	/**
	 * 企业类型
	 */
	private String appType;
}
