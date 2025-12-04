package com.coolcollege.intelligent.service.platform;

import com.coolcollege.intelligent.model.platform.PlatformExpandInfoDO;

import java.util.List;

/**
 * 平台拓展信息配置表
 * 
 * @author xugangkun
 * @date 2021-12-01 14:26:21
 */
public interface PlatformExpandInfoService {

	/**
	 * 根据主键查询
	 * @Param:
	 * @param id
	 * @throws
	 * @return: net.coolcollege.cms.dao.entity.CmsPage
	 * @Author: xugangkun
	 */
	PlatformExpandInfoDO selectById(Long id);

	/**
	 * 根据主键查询
	 * @Param:
	 * @param code
	 * @throws
	 * @return: net.coolcollege.cms.dao.entity.CmsPage
	 * @Author: xugangkun
	 */
	PlatformExpandInfoDO selectByCode(String code);

	/**
	 * 保存
	 * @Param:
	 * @param platformExpandInfo
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void save(PlatformExpandInfoDO platformExpandInfo);

	/**
	 * 根据主键更新
	 * @Param:
	 * @param platformExpandInfo
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void updateById(PlatformExpandInfoDO platformExpandInfo);

	/**
	 * 根据code更新
	 * @Param:
	 * @param platformExpandInfo
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void updateByCode(PlatformExpandInfoDO platformExpandInfo);

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
