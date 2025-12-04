package com.coolcollege.intelligent.model.user;

import lombok.Data;

import java.util.Date;



/**
 * 用户人事状态历史表
 * 
 * @author xugangkun
 * @date 2022-03-02 10:25:49
 */
@Data
public class UserPersonnelStatusHistoryDO {

	/**
	* 主键
	*/
	private Long id;
	/**
	* 状态名称
	*/
	private String statusName;
	/**
	* 用户id
	*/
	private String userId;
	/**
	* 备注
	*/
	private String remarks;
	/**
	* 状态有效时间,以天为最小单位
	*/
	private String effectiveTime;
	/**
	* 创建时间
	*/
	private Date createTime;
	/**
	* 创建人id
	*/
	private String createUserId;
	/**
	* 创建人名字
	*/
	private String createUserName;
	/**
	* 更新时间
	*/
	private Date updateTime;
	/**
	* 更新人id
	*/
	private String updateUserId;
	/**
	* 更新人名称
	*/
	private String updateUserName;

}
