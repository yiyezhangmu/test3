package com.coolcollege.intelligent.model.platform;

import lombok.Data;

import java.util.Date;



/**
 * 平台拓展信息配置表
 * @author xugangkun
 * @date 2021-12-01 14:26:21
 */
@Data
public class PlatformExpandInfoDO {
	private static final long serialVersionUID = 1L;
	
	/**
	* 自增id
	*/
	private Long id;
	/**
	* 代码
	*/
	private String code;
	/**
	* 详细名称
	*/
	private String name;
	/**
	 * 是否已经激活, 1表示已激活, 0表示未激活
	 */
	private Boolean valid;
	/**
	* 描述
	*/
	private String remark;
	/**
	* 内容
	*/
	private String content;
	/**
	* 创建时间
	*/
	private Date createTime;
	/**
	* 更新时间
	*/
	private Date updateTime;

}
