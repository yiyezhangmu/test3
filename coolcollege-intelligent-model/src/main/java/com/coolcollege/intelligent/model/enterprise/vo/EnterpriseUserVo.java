package com.coolcollege.intelligent.model.enterprise.vo;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.position.dto.PositionDTO;
import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO;
import lombok.Data;

import java.util.List;
/**
 * @ClassName EnterpriseUserVo
 * @Description 用户信息
 * @author 首亮
 */
@Data
public class EnterpriseUserVo extends EnterpriseUserDO {
    /**
     * 人员岗位
     */
    private String userPosition;
    /**
     * 人员部门
     */
    private String userDepartment;
    /**
     * 用户所在的部门（多个部门）
     */
    private List<PositionDTO> userPositions;
    /**
     * 用户的多个岗位
     */
    private List<SysDepartmentDO> userDepartments;

    /**
     * 区域门店权限
     */

    private List<AuthRegionStoreUserDTO> authRegionStoreList;
}
