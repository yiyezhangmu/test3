package com.coolcollege.intelligent.service.position;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.position.dto.PositionDTO;
import com.coolcollege.intelligent.model.position.queryDto.PositionQueryDTO;
import com.coolcollege.intelligent.model.system.dto.RoleDTO;

import java.util.List;

public interface PositionService {

    /**
     * 查询岗位列表
     *
     * @param enterpriseId
     * @param positionQueryDTO
     * @return
     */
    List<PositionDTO> getPositionList(String enterpriseId, PositionQueryDTO positionQueryDTO);

    List<RoleDTO> getPositionPageList(String enterpriseId, PositionQueryDTO positionQueryDTO);


    /**
     * 设置用户的其他相关信息（部门、岗位）
     *
     * @param enterpriseId
     * @param users
     */
    void setOtherInfoForUsers(String enterpriseId, List<? extends EnterpriseUserDTO> users);

}
