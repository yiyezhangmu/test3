package com.coolcollege.intelligent.service.bosspackage;


import com.coolcollege.intelligent.model.bosspackage.BusinessModuleDO;
import com.coolcollege.intelligent.model.bosspackage.dto.BusinessModuleDTO;
import com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleDetailVO;
import com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * 业务模块
 * @author xugk
 */
public interface BusinessModuleService {

    /**
     * 通过业务模块名称获得业务模块
     * @param moduleName
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.bosspackage.BusinessModuleDO
     * @date: 2022/3/29 15:01
     */
    List<BusinessModuleDO> selectByModuleName(String moduleName);

    /**
     * 添加业务模块
     * @param businessModuleDTO
     * @param user
     * @author: xugangkun
     * @return int
     * @date: 2022/3/23 15:41
     */
    void addBusinessModule(BusinessModuleDTO businessModuleDTO, CurrentUser user);

    /**
     * 添加业务模块
     * @param businessModuleDTO
     * @param user
     * @author: xugangkun
     * @return int
     * @date: 2022/3/23 15:41
     */
    void updateBusinessModule(BusinessModuleDTO businessModuleDTO, CurrentUser user);

    /**
     * 获得业务模块列表
     * @param pageNum
     * @param pageSize
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleVO>
     * @date: 2022/3/23 11:15
     */
    List<BusinessModuleVO> getBusinessModuleList(Integer pageNum, Integer pageSize);

    /**
     * 获得业务模块详情
     * @param moduleId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleDetailVO
     * @date: 2022/3/28 14:25
     */
    BusinessModuleDetailVO getBusinessModuleDetail(Long moduleId);

    /**
     * 获得有效的业务模块列表
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleVO>
     * @date: 2022/3/23 11:15
     */
    List<BusinessModuleVO> getValidModuleList();

    /**
     * 根据主键修改状态
     * @param status
     * @param moduleId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/23 14:22
     */
    void updateModuleStatus(String status, Long moduleId);

    /**
     * 默认删除方法，根据主键物理删除
     * @param moduleId
     * @author: xugangkun
     * @return int
     * @date: 2022/3/23 14:42
     */
    int deleteByPrimaryKey(Long moduleId);

}
