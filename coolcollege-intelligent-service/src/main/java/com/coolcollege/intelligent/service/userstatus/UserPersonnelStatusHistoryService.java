package com.coolcollege.intelligent.service.userstatus;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.user.UserPersonnelStatusHistoryDO;
import com.coolcollege.intelligent.model.user.dto.UserPersonnelStatusHistoryExportRequest;
import com.coolcollege.intelligent.model.user.dto.UserPersonnelStatusHistoryReportDTO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusHistoryReportVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 用户人事状态历史表
 * 
 * @author xugangkun
 * @date 2022-03-02 10:31:57
 */
public interface UserPersonnelStatusHistoryService {

	/**
	 * 根据主键查询
	 * @param eid
	 * @param id
	 * @throws
	 * @return: net.coolcollege.cms.dao.entity.CmsPage
	 * @Author: xugangkun
	 */
	UserPersonnelStatusHistoryDO selectById(String eid, Long id);

	/**
	 * 根据userId以及有效时间查询
	 * @param eid
	 * @param userId
	 * @param effectiveTime
	 * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
	 * @Author: xugangkun
	 * @Date: 2021/3/20 14:32
	 */
	UserPersonnelStatusHistoryDO selectByUserIdAndEffectiveTime(String eid, String userId, String effectiveTime);

	/**
	 * 根据userId以及有效时间查询
	 * @param eid
	 * @param userIds
	 * @param query
	 * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
	 * @Author: xugangkun
	 * @Date: 2021/3/20 14:32
	 */
	PageInfo<UserPersonnelStatusHistoryReportVO> getStatusHistoryReport(String eid, List<String> userIds, UserPersonnelStatusHistoryReportDTO query);

	/**
	 * 保存
	 * @param eid
	 * @param userPersonnelStatusHistory
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void save(String eid, UserPersonnelStatusHistoryDO userPersonnelStatusHistory);

	/**
	 * 批量更新用户人事状态
	 * @param eid
	 * @param list
	 * @author: xugangkun
	 * @return void
	 * @date: 2022/3/3 11:12
	 */
	void batchInsertOrUpdate(String eid, List<UserPersonnelStatusHistoryDO> list);

	/**
	 * 根据主键更新
	 * @param eid
	 * @param userPersonnelStatusHistory
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void updateById(String eid, UserPersonnelStatusHistoryDO userPersonnelStatusHistory);

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
	 * 根据主键id批量删除
	 * @param eid
	 * @param ids
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void deleteBatchByIds(String eid, List<Long> ids);

	/**
	 * 导出人事状态列表
	 * @param eid
	 * @param request
	 * @param user
	 * @author: xugangkun
	 * @return com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO
	 * @date: 2022/3/9 15:23
	 */
	ImportTaskDO exportUserPersonnelStatusHistory(String eid, UserPersonnelStatusHistoryExportRequest request, CurrentUser user);
}
