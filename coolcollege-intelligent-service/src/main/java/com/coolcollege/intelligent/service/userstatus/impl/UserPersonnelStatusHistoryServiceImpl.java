package com.coolcollege.intelligent.service.userstatus.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.PageUtil;
import com.coolcollege.intelligent.dao.userstatus.dao.UserPersonnelStatusHistoryDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.user.UserPersonnelStatusHistoryDO;
import com.coolcollege.intelligent.model.user.dto.UserPersonnelStatusHistoryExportRequest;
import com.coolcollege.intelligent.model.user.dto.UserPersonnelStatusHistoryReportDTO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusHistoryReportVO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusHistoryVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.userstatus.UserPersonnelStatusHistoryService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author xugk
 */
@Service("userPersonnelStatusHistoryService")
public class UserPersonnelStatusHistoryServiceImpl implements UserPersonnelStatusHistoryService {
	@Autowired
	private UserPersonnelStatusHistoryDao userPersonnelStatusHistoryDao;
	@Autowired
	private EnterpriseUserService enterpriseUserService;
	@Autowired
	private ExportUtil exportUtil;
	
	@Override
	public UserPersonnelStatusHistoryDO selectById(String eid, Long id){
		return userPersonnelStatusHistoryDao.selectById(eid, id);
	}

	@Override
	public UserPersonnelStatusHistoryDO selectByUserIdAndEffectiveTime(String eid, String userId, String effectiveTime) {
		return userPersonnelStatusHistoryDao.selectByUserIdAndEffectiveTime(eid, userId, effectiveTime);
	}

	@Override
	public PageInfo<UserPersonnelStatusHistoryReportVO> getStatusHistoryReport(String eid, List<String> allUserIds, UserPersonnelStatusHistoryReportDTO query) {

		List<UserPersonnelStatusHistoryReportVO> report = new ArrayList<>();
		if (CollectionUtils.isEmpty(allUserIds)) {
			return new PageInfo<>();
		}
		//在此处进行allUserIds的切割分页
		List<String> queryList = PageUtil.startPage(allUserIds, query.getPageNum(), query.getPageSize());
		//根据用户分页
		List<EnterpriseUserDO> userList = enterpriseUserService.selectUsersByUserIds(eid, queryList);
		//赛选出用户id
		List<String> queryUserIds = new ArrayList<>();
		userList.forEach(user -> {
			UserPersonnelStatusHistoryReportVO vo = new UserPersonnelStatusHistoryReportVO();
			vo.setUserId(user.getUserId());
			vo.setAvatar(user.getAvatar());
			vo.setUserName(user.getName());
			report.add(vo);
			queryUserIds.add(user.getUserId());
		});
		String starTimeStr = DateUtil.format(query.getStartTime());
		String endTimeStr = DateUtil.format(query.getEndTime());
		if (CollectionUtils.isEmpty(queryUserIds)) {
			return new PageInfo<>();
		}
		List<UserPersonnelStatusHistoryVO> histories = userPersonnelStatusHistoryDao.getStatusHistoryReport(eid, queryUserIds, starTimeStr, endTimeStr);
		//根据userId分组
		Map<String, List<UserPersonnelStatusHistoryVO>> userGroup = ListUtils.emptyIfNull(histories)
				.stream()
				.collect(Collectors.groupingBy(UserPersonnelStatusHistoryVO::getUserId));
		report.forEach(re -> {
			re.setStatusHistoryList(userGroup.get(re.getUserId()));
		});
		PageInfo<UserPersonnelStatusHistoryReportVO> pageInfo = new PageInfo();
		pageInfo.setTotal(allUserIds.size());
		pageInfo.setList(report);
		return pageInfo;
	}

	@Override
	public void save(String eid, UserPersonnelStatusHistoryDO userPersonnelStatusHistory){
		userPersonnelStatusHistoryDao.save(eid, userPersonnelStatusHistory);
	}

	@Override
	public void batchInsertOrUpdate(String eid, List<UserPersonnelStatusHistoryDO> list) {
		userPersonnelStatusHistoryDao.batchInsertOrUpdate(eid, list);
	}
	
	@Override
	public void updateById(String eid, UserPersonnelStatusHistoryDO userPersonnelStatusHistory){
		userPersonnelStatusHistoryDao.updateById(eid, userPersonnelStatusHistory);
	}
	
	@Override
	public void deleteById(String eid, Long id){
		userPersonnelStatusHistoryDao.deleteById(eid, id);
	}
	
	@Override
	public void deleteBatchByIds(String eid, List<Long> ids){
		userPersonnelStatusHistoryDao.deleteBatchByIds(eid, ids);
	}

	@Override
	public ImportTaskDO exportUserPersonnelStatusHistory(String eid, UserPersonnelStatusHistoryExportRequest request, CurrentUser user) {
		request.setExportServiceEnum(ExportServiceEnum.EXPORT_USER_PERSONNEL_STATUS_HISTORY);
		return exportUtil.exportFile(eid, request, UserHolder.getUser().getDbName());
	}

}
