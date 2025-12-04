package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseDictDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseDictBaseVO;
import com.coolcollege.intelligent.model.enums.BusinessTypeEnum;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusVO;

import java.util.List;

/**
 * 用户人事状态
 * 
 * @author xugangkun
 * @date 2022-03-02 10:31:57
 */
public interface EnterpriseDictService {

	/**
	 * 根据主键查询
	 * @param eid
	 * @param id
	 * @throws
	 * @return: net.coolcollege.cms.dao.entity.CmsPage
	 * @Author: xugangkun
	 */
	EnterpriseDictDO selectById(String eid, Long id);

	/**
	 * 根据业务类型和业务详情查询
	 * @param eid
	 * @param businessType
	 * @param businessValue
	 * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
	 * @Author: xugangkun
	 * @Date: 2021/3/20 14:32
	 */
	EnterpriseDictDO selectByTypeAndValue(String eid, String businessType, String businessValue);

	/**
	 * 获得所有状态
	 * @param eid
	 * @param businessType
	 * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
	 * @Author: xugangkun
	 * @Date: 2021/3/20 14:32
	 */
	List<UserPersonnelStatusVO> selectAllByType(String eid, String businessType);

	/**
	 * 保存
	 * @param eid
	 * @param userPersonnelStatus
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void save(String eid, EnterpriseDictDO userPersonnelStatus);

	/**
	 * 根据主键更新
	 * @param eid
	 * @param userPersonnelStatus
	 * @throws
	 * @return: void
	 * @Author: xugangkun
	 */
	void updateById(String eid, EnterpriseDictDO userPersonnelStatus);

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
	 * 根据业务类型查询字典表
	 * @param eid 企业id
	 * @param businessType 业务类型
	 * @return List<? extends EnterpriseDictBaseVO>
	 */
	List<? extends EnterpriseDictBaseVO> selectByType(String eid, BusinessTypeEnum businessType);
}
