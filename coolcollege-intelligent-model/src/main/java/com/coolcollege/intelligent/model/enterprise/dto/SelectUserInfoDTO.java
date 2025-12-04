package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentStoreVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/2/2 10:20
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SelectUserInfoDTO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户工号
     */
    private String jobnumber;

    /**
     * 部门信息
     */
    private List<SelectUserDeptDTO> deptInfo;

    /**
     * 职位信息
     */
    private SelectUserRoleDTO positionInfo;

    /**
     * 人员区域信息
     */
    private List<SelectComponentRegionVO> regionVos;

    /**
     * 人员门店信息
     */
    private List<SelectComponentStoreVO> storeVos;
}
