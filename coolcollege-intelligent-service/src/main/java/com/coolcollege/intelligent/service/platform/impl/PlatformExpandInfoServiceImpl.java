package com.coolcollege.intelligent.service.platform.impl;

import com.coolcollege.intelligent.dao.platform.dao.PlatformExpandInfoDao;
import com.coolcollege.intelligent.model.platform.PlatformExpandInfoDO;
import com.coolcollege.intelligent.service.platform.PlatformExpandInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;



/**
 * @author xugk
 */
@Service("platformExpandInfoService")
public class PlatformExpandInfoServiceImpl implements PlatformExpandInfoService {

	@Resource
	private PlatformExpandInfoDao platformExpandInfoDao;
	
	@Override
	public PlatformExpandInfoDO selectById(Long id){
		return platformExpandInfoDao.selectById(id);
	}

	@Override
	public PlatformExpandInfoDO selectByCode(String code) {
		return platformExpandInfoDao.selectByCode(code);
	}

	@Override
	public void save(PlatformExpandInfoDO platformExpandInfo){
		platformExpandInfoDao.save(platformExpandInfo);
	}
	
	@Override
	public void updateById(PlatformExpandInfoDO platformExpandInfo){
		platformExpandInfoDao.updateById(platformExpandInfo);
	}

	@Override
	public void updateByCode(PlatformExpandInfoDO platformExpandInfo){
		platformExpandInfoDao.updateByCode(platformExpandInfo);
	}
	
	@Override
	public void deleteById(Long id){
		platformExpandInfoDao.deleteById(id);
	}
	
	@Override
	public void deleteBatchByIds(List<Long> ids){
		platformExpandInfoDao.deleteBatchByIds(ids);
	}

}
