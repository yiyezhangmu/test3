package com.coolcollege.intelligent.dao.platform.dao;

import com.coolcollege.intelligent.dao.platform.PlatformExpandInfoMapper;
import com.coolcollege.intelligent.model.platform.PlatformExpandInfoDO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author xugk
 */
@Repository
public class PlatformExpandInfoDao {

	@Resource
	private PlatformExpandInfoMapper platformExpandInfoMapper;
	
	public PlatformExpandInfoDO selectById(Long id){
		return platformExpandInfoMapper.selectById(id);
	}

	public PlatformExpandInfoDO selectByCode(String code){
		return platformExpandInfoMapper.selectByCode(code);
	}
	
	public void save(PlatformExpandInfoDO platformExpandInfo){
		platformExpandInfoMapper.save(platformExpandInfo);
	}
	
	public void updateById(PlatformExpandInfoDO platformExpandInfo){
		platformExpandInfoMapper.updateById(platformExpandInfo);
	}

	public void updateByCode(PlatformExpandInfoDO platformExpandInfo){
		platformExpandInfoMapper.updateByCode(platformExpandInfo);
	}
	
	public void deleteById(Long id){
		platformExpandInfoMapper.deleteById(id);
	}
	
	public void deleteBatchByIds(List<Long> ids){
		platformExpandInfoMapper.deleteBatchByIds(ids);
	}

}
