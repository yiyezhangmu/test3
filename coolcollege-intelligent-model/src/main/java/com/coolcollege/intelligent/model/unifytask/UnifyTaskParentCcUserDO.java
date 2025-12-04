package com.coolcollege.intelligent.model.unifytask;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;



/**
 * 父任务与抄送人映射表
 * 
 * @author xugangkun
 * @date 2021-11-17 15:06:44
 */
@Data
@NoArgsConstructor
public class UnifyTaskParentCcUserDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	* 主键
	*/
	private Long id;
	/**
	* 父任务id
	*/
	private Long unifyTaskId;
	/**
	* 任务名称
	*/
	private String taskName;
	/**
	* 任务类型
	*/
	private String taskType;
	/**
	* 抄送人id
	*/
	private String ccUserId;
	/**
	* 父任务状态
	*/
	private String parentStatus;
	/**
	 * 开始时间
	 */
	private Long beginTime;
	/**
	 * 结束时间
	 */
	private Long endTime;

	public UnifyTaskParentCcUserDO(Long unifyTaskId, String taskName, String taskType, String ccUserId, String parentStatus, Long beginTime, Long endTime) {
		this.unifyTaskId = unifyTaskId;
		this.taskName = taskName;
		this.taskType = taskType;
		this.ccUserId = ccUserId;
		this.parentStatus = parentStatus;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}
}
