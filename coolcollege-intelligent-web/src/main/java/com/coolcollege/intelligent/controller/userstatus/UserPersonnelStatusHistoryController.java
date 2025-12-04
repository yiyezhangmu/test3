package com.coolcollege.intelligent.controller.userstatus;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.user.UserPersonnelStatusHistoryDO;
import com.coolcollege.intelligent.model.user.dto.*;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusHistoryReportVO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusHistoryVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.userstatus.UserPersonnelStatusHistoryService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 用户人事状态历史表
 * 
 * @author xugangkun
 * @email 670809626@qq.com
 * @date 2022-03-02 10:31:57
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/userStatusHistory")
public class UserPersonnelStatusHistoryController {

	@Autowired
	private UserPersonnelStatusHistoryService userPersonnelStatusHistoryService;

	@Autowired
	private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

	@Autowired
	private RedisUtilPool redis;
	
	/**
	 * 获得当前装填
	 */
	@GetMapping("/getCurrentStatus")
	public ResponseResult getCurrentStatus(@PathVariable(value = "enterprise-id") String eid,
										   @RequestParam(value = "userId") String userId){
		//查询列表数据
		DataSourceHelper.changeToMy();
		UserPersonnelStatusHistoryDO newStatus = userPersonnelStatusHistoryService.selectByUserIdAndEffectiveTime(eid, userId, DateUtil.format(new Date()));
		UserPersonnelStatusHistoryVO vo = new UserPersonnelStatusHistoryVO();
		vo.setUserId(userId);
		if (newStatus == null) {
			vo.setStatusName(Constants.PERSONNEL_STATUS_NORMAL);
			vo.setEffectiveTime(DateUtil.format(new Date()));
		} else {
			vo.setStatusName(newStatus.getStatusName());
			vo.setRemarks(newStatus.getRemarks());
			vo.setEffectiveTime(newStatus.getEffectiveTime());
		}

		return ResponseResult.success(vo);
	}

	/**
	 * 用户人事状态历史报表
	 * @param eid
	 * @param query
	 * @author: xugangkun
	 * @return com.coolcollege.intelligent.common.response.ResponseResult
	 * @date: 2022/3/4 10:48
	 */
	@PostMapping("/report")
	public ResponseResult<PageInfo<UserPersonnelStatusHistoryReportVO>> list(@PathVariable(value = "enterprise-id") String eid,
																			 @RequestBody UserPersonnelStatusHistoryReportDTO query){
		DataSourceHelper.changeToMy();
		CurrentUser user = UserHolder.getUser();
		//参数校验
		if (CollectionUtils.isEmpty(query.getRoleList()) && CollectionUtils.isEmpty(query.getUserList())) {
			return ResponseResult.fail(ErrorCodeEnum.EMPTY_REPORT_PARAM);
		}
		List<Date> dateList = DateUtils.getBetweenDate(query.getStartTime(), query.getEndTime());
		if (Constants.MAX_PERSONNEL_STATUS_TIME < dateList.size()) {
			return ResponseResult.fail(ErrorCodeEnum.TIME_OUT_OF_MAX);
		}
		if (Constants.MAX_PAGE_SIZE < query.getPageSize()) {
			return ResponseResult.fail(ErrorCodeEnum.OUT_OF_MAX_PAGE_SIZE);
		}
		redis.setString(Constants.PERSONNEL_STATUS_PRE + user.getUserId() + user.getEnterpriseId(), JSONObject.toJSONString(query));
		List<String> allUserIdList = getAllUserIdList(eid, user, query);
		PageInfo<UserPersonnelStatusHistoryReportVO> result = userPersonnelStatusHistoryService.getStatusHistoryReport(eid, allUserIdList, query);
		return ResponseResult.success(result);
	}

	/**
	 * 用户人事状态历史报表
	 * @param eid
	 * @param query
	 * @author: xugangkun
	 * @return com.coolcollege.intelligent.common.response.ResponseResult
	 * @date: 2022/3/4 10:48
	 */
	@PostMapping("/export")
	public ResponseResult export(@PathVariable(value = "enterprise-id") String eid,
								 @RequestBody UserPersonnelStatusHistoryReportDTO query){
		DataSourceHelper.changeToMy();
		CurrentUser user = UserHolder.getUser();
		//参数校验
		if (CollectionUtils.isEmpty(query.getRoleList()) && CollectionUtils.isEmpty(query.getUserList())) {
			return ResponseResult.fail(ErrorCodeEnum.EMPTY_REPORT_PARAM);
		}
		if (Constants.MAX_PAGE_SIZE < query.getPageSize()) {
			return ResponseResult.fail(ErrorCodeEnum.OUT_OF_MAX_PAGE_SIZE);
		}
		List<String> allUserIdList = getAllUserIdList(eid, user, query);
		UserPersonnelStatusHistoryExportRequest request = new UserPersonnelStatusHistoryExportRequest();
		request.setAllUserIdList(allUserIdList);
		request.setQuery(query);
		ImportTaskDO result = userPersonnelStatusHistoryService.exportUserPersonnelStatusHistory(eid, request, user);
		return ResponseResult.success(result);
	}

	private List<String> getAllUserIdList(String eid, CurrentUser user, UserPersonnelStatusHistoryReportDTO query) {

		Set<String> allUserIdSet = new HashSet<>();
		if (CollectionUtils.isNotEmpty(query.getUserList())) {
			List<String> userIds = query.getUserList().stream().map(UserSimpleDTO::getUserId).collect(Collectors.toList());
			allUserIdSet.addAll(userIds);
		}
		if (CollectionUtils.isNotEmpty(query.getRoleList())) {
			List<Long> roleIds = query.getRoleList().stream().map(RoleSimpleDTO::getRoleId).collect(Collectors.toList());
			List<String> roleUsers = enterpriseUserRoleMapper.selectUserIdsByRoleIdList(eid, roleIds);
			allUserIdSet.addAll(roleUsers);
		}
		List<String> allUserIdList = new ArrayList<>();
		//添加allUserIdList的排序
		allUserIdList.addAll(allUserIdSet);
		Collections.sort(allUserIdList);
		return allUserIdList;
	}

	/**
	 * 获得缓存的人事报表参数
	 */
	@GetMapping("/getReportParam")
	public ResponseResult getReportParam(@PathVariable(value = "enterprise-id") String eid){
		//查询列表数据
		DataSourceHelper.changeToMy();
		CurrentUser user = UserHolder.getUser();
		String queryStr = redis.getString(Constants.PERSONNEL_STATUS_PRE + user.getUserId() + user.getEnterpriseId());
		if (StringUtils.isBlank(queryStr)) {
			return ResponseResult.success(false);
		}
		UserPersonnelStatusHistoryReportDTO result = JSONObject.parseObject(queryStr, UserPersonnelStatusHistoryReportDTO.class);
		return ResponseResult.success(result);
	}

	/**
	 * 改变用户状态
	 * @param eid
	 * @author: xugangkun
	 * @return com.coolcollege.intelligent.common.response.ResponseResult
	 * @date: 2022/3/2 15:30
	 */
	@PostMapping("/changePersonnelStatus")
	@SysLog(func = "修改人事状态", opModule = OpModuleEnum.ENTERPRISE_USER, opType = OpTypeEnum.EDIT_USER_STATUS)
	public ResponseResult changePersonnelStatus(@PathVariable(value = "enterprise-id") String eid,
								 @RequestBody @Valid UserPersonnelStatusHistoryDTO userPersonnelStatusHistoryDTO) throws Exception {
		DataSourceHelper.changeToMy();
		List<Date> dateList = DateUtils.getBetweenDate(userPersonnelStatusHistoryDTO.getStartTime(), userPersonnelStatusHistoryDTO.getEndTime());
		if (Constants.MAX_PERSONNEL_STATUS_TIME < dateList.size()) {
			return ResponseResult.fail(ErrorCodeEnum.TIME_OUT_OF_MAX);
		}
		CurrentUser user = UserHolder.getUser();
		DateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_DAY);
		//这里添加更改状态的权限校验
		Date date = new SimpleDateFormat(DateUtils.DATE_FORMAT_DAY).parse(DateUtil.format(new Date()));
		//添加判断是本人
		if (!Role.MASTER.getId().equals(String.valueOf(user.getSysRoleDO().getId()))) {
			if (!userPersonnelStatusHistoryDTO.getUserId().equals(user.getUserId())) {
				return ResponseResult.fail(ErrorCodeEnum.NO_PERMISSION);
			}
			if (userPersonnelStatusHistoryDTO.getStartTime().before(date)) {
				return ResponseResult.fail(ErrorCodeEnum.TIME_NOT_ALLOWED);
			}
		}

		List<UserPersonnelStatusHistoryDO> historyDOS = new ArrayList<>();
		dateList.forEach(da -> {
			UserPersonnelStatusHistoryDO history = new UserPersonnelStatusHistoryDO();
			history.setEffectiveTime(dateFormat.format(da));
			history.setUserId(userPersonnelStatusHistoryDTO.getUserId());
			history.setStatusName(userPersonnelStatusHistoryDTO.getStatusName());
			history.setCreateUserId(user.getUserId());
			history.setCreateUserName(user.getName());
			history.setCreateTime(new Date());
			history.setUpdateUserId(user.getUserId());
			history.setRemarks(userPersonnelStatusHistoryDTO.getRemarks());
			historyDOS.add(history);
		});
		userPersonnelStatusHistoryService.batchInsertOrUpdate(eid, historyDOS);
		return ResponseResult.success();
	}


}
