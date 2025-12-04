package com.coolcollege.intelligent.service.unifytask.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.unifytask.dao.UnifyTaskParentCcUserDao;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentCcUserDO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskCcUserMsgDTO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentCcUserService;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author xugk
 */
@Service("unifyTaskParentCcUserService")
@Slf4j
public class UnifyTaskParentCcUserServiceImpl implements UnifyTaskParentCcUserService {
	@Autowired
	private UnifyTaskParentCcUserDao unifyTaskParentCcUserDao;

	@Autowired
	private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

	@Resource
	private EnterpriseConfigService enterpriseConfigService;

	@Resource
	private SimpleMessageService simpleMessageService;

	@Override
	public UnifyTaskParentCcUserDO selectById(String eid, Long id){
		return unifyTaskParentCcUserDao.selectById(eid, id);
	}

	@Override
	public List<UnifyTaskParentCcUserDO> selectByCcUserId(String eid, String userId, DisplayQuery query){
		return unifyTaskParentCcUserDao.selectByCcUserId(eid, userId, query);
	}

	@Override
	public Integer selectDisplayParentStatistics(String enterpriseId, String userId, String taskType, String status) {
		return unifyTaskParentCcUserDao.selectDisplayParentStatistics(enterpriseId, userId, taskType, status);
	}

	@Override
	public void save(String eid, UnifyTaskParentCcUserDO unifyTaskParentCcUser){
		unifyTaskParentCcUserDao.save(eid, unifyTaskParentCcUser);
	}

	@Override
	public void batchInsertOrUpdate(String eid, List<UnifyTaskParentCcUserDO> list) {
		unifyTaskParentCcUserDao.batchInsertOrUpdate(eid, list);
	}

	@Override
	public void deleteById(String eid, Long id){
		unifyTaskParentCcUserDao.deleteById(eid, id);
	}

	@Override
	public void deleteByUnifyTaskId(String eid, Long unifyTaskId){
		unifyTaskParentCcUserDao.deleteByUnifyTaskId(eid, unifyTaskId);
	}

	@Override
	public void updateTaskParentStatus(String eid, Long taskId, String parentStatus) {
		unifyTaskParentCcUserDao.updateTaskParentStatus(eid, taskId, parentStatus);
	}

	@Override
	public void setDisplayCcUser(String eid, UnifyTaskBuildDTO task) {
		//只有陈列任务根据父任务抄送   2021-11-18 xgk
		if (!TaskTypeEnum.TB_DISPLAY_TASK.getCode().equals(task.getTaskType())) {
			return;
		}
		UnifyTaskCcUserMsgDTO ccUserMsg = new UnifyTaskCcUserMsgDTO();
		ccUserMsg.setEid(eid);
		ccUserMsg.setBeginTime(task.getBeginTime());
		ccUserMsg.setEndTime(task.getEndTime());
		ccUserMsg.setTaskName(task.getTaskName());
		ccUserMsg.setTaskId(task.getTaskId());
		ccUserMsg.setTaskType(task.getTaskType());
		ccUserMsg.setProcess(task.getProcess());
		simpleMessageService.send(JSONObject.toJSONString(ccUserMsg), RocketMqTagEnum.DISPLAY_CC_USER_QUEUE);
	}

}
