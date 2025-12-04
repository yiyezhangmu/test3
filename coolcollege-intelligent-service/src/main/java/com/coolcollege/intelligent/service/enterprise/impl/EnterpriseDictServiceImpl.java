package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseDictDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDictDO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusVO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseDictService;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseDictBaseVO;
import com.coolcollege.intelligent.model.enums.BusinessTypeEnum;
import com.coolcollege.intelligent.model.newstore.vo.NsStoreTypeVO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusVO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseDictService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;


/**
 * @author xugk
 */
@Service("userPersonnelStatusService")
public class EnterpriseDictServiceImpl implements EnterpriseDictService {
	@Autowired
	private EnterpriseDictDao enterpriseDictDao;
	
	@Override
	public EnterpriseDictDO selectById(String eid, Long id){
		return enterpriseDictDao.selectById(eid, id);
	}

	@Override
	public EnterpriseDictDO selectByTypeAndValue(String eid, String businessType, String businessValue) {
		return enterpriseDictDao.selectByTypeAndValue(eid, businessType, businessValue);
	}

	@Override
	public List<UserPersonnelStatusVO> selectAllByType(String eid, String businessType) {
		return enterpriseDictDao.selectAllByType(eid, businessType);
	}

	@Override
	public void save(String eid, EnterpriseDictDO userPersonnelStatus){
		enterpriseDictDao.save(eid, userPersonnelStatus);
	}
	
	@Override
	public void updateById(String eid, EnterpriseDictDO userPersonnelStatus){
		enterpriseDictDao.updateById(eid, userPersonnelStatus);
	}
	
	@Override
	public void deleteById(String eid, Long id){
		enterpriseDictDao.deleteById(eid, id);
	}
	
	@Override
	public void deleteBatchByIds(String eid, List<Long> ids){
		enterpriseDictDao.deleteBatchByIds(eid, ids);
	}


	@Override
	public List<? extends EnterpriseDictBaseVO> selectByType(String eid, BusinessTypeEnum businessType) {
		List<EnterpriseDictDO> dictDOList = enterpriseDictDao.selectByType(eid, businessType.getCode());
		if(CollectionUtils.isEmpty(dictDOList)) {
			return Lists.newArrayList();
		}
		List<? extends EnterpriseDictBaseVO> voList;
		switch (businessType){
			case NEW_STORE_TYPE:
				voList = dictDOList.stream().map(this::parseDictDoToNsStoreVo).collect(Collectors.toList());
				break;
			default:
				voList = Lists.newArrayList();
		}
		return voList;
	}

	/**
	 * 通用字典DO转门店字典vo
	 * @param dictDO
	 * @return
	 */
	private NsStoreTypeVO parseDictDoToNsStoreVo(EnterpriseDictDO dictDO) {
		NsStoreTypeVO storeTypeVO = new NsStoreTypeVO();
		storeTypeVO.setNewStoreType(dictDO.getBusinessValue());
		storeTypeVO.setId(dictDO.getId());
		storeTypeVO.setValue(dictDO.getBusinessValue());
		return storeTypeVO;
	}

}
