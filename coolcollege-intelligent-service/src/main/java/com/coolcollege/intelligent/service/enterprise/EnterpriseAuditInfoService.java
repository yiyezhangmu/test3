package com.coolcollege.intelligent.service.enterprise;


import com.coolcollege.intelligent.model.enterprise.EnterpriseAuditInfoDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseAuditVO;

import java.util.List;

/**
 * 企业审核表
 * @author xugangkun
 * @date 2021-07-19 16:27:52
 */
public interface EnterpriseAuditInfoService {

	/**
	 * 根据主键查询
	 * @Param:
	 * @param id
	 * @throws
	 * @return: net.coolcollege.cms.dao.entity.CmsPage
	 * @Author: xugangkun
	 */
	EnterpriseAuditInfoDO selectById(Long id);

	/**
	 * 企业审核列表
	 * @param pageSize
	 * @param pageNumber
	 * @param enterpriseName 企业名称
	 * @param auditStatus 审核状态
	 * @author: xugangkun
	 * @return java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseAuditInfoDO>
	 * @date: 2021/7/20 10:15
	 */
	List<EnterpriseAuditVO> enterpriseAuditList(Integer pageSize, Integer pageNumber, String enterpriseName, Integer auditStatus);

	/**
	 * 保存
	 * @Param:
	 * @param EnterpriseAuditInfoDO
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void save(EnterpriseAuditInfoDO EnterpriseAuditInfoDO);

	/**
	 * 根据主键更新
	 * @Param:
	 * @param EnterpriseAuditInfoDO
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void updateById(EnterpriseAuditInfoDO EnterpriseAuditInfoDO);

	/**
	 * 审核企业
	 * @param entity 实体
	 * @return: void
	 * @Author: xugangkun
	 */
	void auditEnterprise(EnterpriseAuditInfoDO entity);

	/**
	 * 根据主键删除
	 * @Param:
	 * @param id
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void deleteById(Long id);

	/**
	 * 根据主键id批量删除
	 * @Param:
	 * @param ids
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void deleteBatchByIds(List<Long> ids);
}
