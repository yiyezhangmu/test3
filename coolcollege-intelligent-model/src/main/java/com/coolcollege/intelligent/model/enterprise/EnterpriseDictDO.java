package com.coolcollege.intelligent.model.enterprise;

import lombok.Data;

import java.util.Date;



/**
 * 用户人事状态
 * 
 * @author xugangkun
 * @date 2022-03-02 10:25:50
 */
@Data
public class EnterpriseDictDO {
	/**
	* 主键
	*/
	private Long id;
	/**
	* 业务类型
	*/
	private String businessType;
	/**
	* 业务详情
	*/
	private String businessValue;
	/**
	* 备注
	*/
	private String remarks;
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
