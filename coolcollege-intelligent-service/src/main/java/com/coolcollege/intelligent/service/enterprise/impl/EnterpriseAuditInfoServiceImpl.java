package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseAuditInfoMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseAuditInfoDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseAuditVO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseAuditInfoService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 企业审核表
 * @author xugangkun
 * @date 2021-07-19 16:27:52
 */
@Slf4j
@Service("enterpriseAuditInfoService")
public class EnterpriseAuditInfoServiceImpl implements EnterpriseAuditInfoService {

	@Autowired
	private EnterpriseAuditInfoMapper enterpriseAuditInfoMapper;
	
	@Override
	public EnterpriseAuditInfoDO selectById(Long id){
		return enterpriseAuditInfoMapper.selectById(id);
	}

	@Override
	public List<EnterpriseAuditVO> enterpriseAuditList(Integer pageSize, Integer pageNumber, String enterpriseName, Integer auditStatus) {
		DataSourceHelper.reset();
		PageHelper.startPage(pageNumber,pageSize);
		List<EnterpriseAuditVO> auditList = enterpriseAuditInfoMapper.enterpriseAuditList(enterpriseName, auditStatus);
		return auditList;
	}

	@Override
	public void save(EnterpriseAuditInfoDO enterpriseAuditInfo){
		enterpriseAuditInfoMapper.save(enterpriseAuditInfo);
	}
	
	@Override
	public void updateById(EnterpriseAuditInfoDO enterpriseAuditInfo){
		enterpriseAuditInfoMapper.updateById(enterpriseAuditInfo);
	}

	@Override
	public void auditEnterprise(EnterpriseAuditInfoDO entity) {
		enterpriseAuditInfoMapper.auditEnterprise(entity);
	}

	@Override
	public void deleteById(Long id){
		enterpriseAuditInfoMapper.deleteById(id);
	}
	
	@Override
	public void deleteBatchByIds(List<Long> ids){
		enterpriseAuditInfoMapper.deleteBatchByIds(ids);
	}

}
