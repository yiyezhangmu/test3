package com.coolcollege.intelligent.model.platform.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * 平台拓展信息配置表
 * @author xugangkun
 * @date 2021-12-01 14:26:21
 */
@Data
public class PlatformExpandInfoDTO {
	private static final long serialVersionUID = 1L;
	/**
	* 代码
	*/
	@NotBlank(message = "编码不能为空")
	private String code;
	/**
	* 详细名称
	*/
	private String name;
	/**
	* 描述
	*/
	private String remark;
	/**
	* 内容
	*/
	private String content;
	/**
	* 内容
	*/
	private Boolean valid;

}
