package com.coolcollege.intelligent.service.unifytask;

import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentCcUserDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;

import java.util.List;

/**
 * 父任务与抄送人映射表
 * 
 * @author xugangkun
 * @date 2021-11-17 15:06:44
 */
public interface UnifyTaskParentCcUserService {

	/**
	 * 根据主键查询
	 * @Param:
	 * @param id
	 * @throws
	 * @return: net.coolcollege.cms.dao.entity.CmsPage
	 * @Author: xugangkun
	 */
	UnifyTaskParentCcUserDO selectById(String eid, Long id);

	/**
	 * 根据主键查询
	 * @param eid
	 * @param userId
	 * @param query
	 * @throws
	 * @return: net.coolcollege.cms.dao.entity.CmsPage
	 * @Author: xugangkun
	 */
	List<UnifyTaskParentCcUserDO> selectByCcUserId(String eid, String userId, DisplayQuery query);


	/**
	 * 统计父信息
	 * @param enterpriseId
	 * @param userId
	 * @param taskType
	 * @param status
	 * @author: xugangkun
	 * @return java.lang.Integer
	 * @date: 2021/11/18 18:27
	 */
	Integer selectDisplayParentStatistics(String enterpriseId, String userId, String taskType, String status);

	/**
	 * 保存
	 * @Param:
	 * @param unifyTaskParentCcUser
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void save(String eid, UnifyTaskParentCcUserDO unifyTaskParentCcUser);

	/**
	 * 批量添加父任务抄送人
	 * @param eid
	 * @param list
	 * @author: xugangkun
	 * @return void
	 * @date: 2021/11/18 11:37
	 */
	void batchInsertOrUpdate(String eid, List<UnifyTaskParentCcUserDO> list);

	/**
	 * 根据主键删除
	 * @param eid
	 * @param id
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void deleteById(String eid, Long id);

	/**
	 * 根据父任务id删除
	 * @param eid
	 * @param unifyTaskId
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void deleteByUnifyTaskId(String eid, Long unifyTaskId);

	/**
	 * 设置陈列任务的抄送人
	 * @param eid
	 * @param taskBuildDTO
	 * @author: xugangkun
	 * @return void
	 * @date: 2021/11/18 10:33
	 */
	void setDisplayCcUser(String eid, UnifyTaskBuildDTO taskBuildDTO);

	/**
	 * 更新抄送任务的状态
	 * @param eid
	 * @param taskId
	 * @param parentStatus
	 * @author: xugangkun
	 * @return void
	 * @date: 2021/11/19 14:39
	 */
	void updateTaskParentStatus(String eid, Long taskId, String parentStatus);

}
