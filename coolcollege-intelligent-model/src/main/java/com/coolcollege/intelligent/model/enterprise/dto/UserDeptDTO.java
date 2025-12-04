package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.department.dto.DeptForUserDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/12/21 16:07
 */
@Data
public class UserDeptDTO {

    private String userId;

    private String name;

    private String avatar;

    private Boolean isAdmin;

    private String enterpriseName;

    private String mobile;

    private String email;

    private String unionid;

    /**
     * 工号
     */
    private String jobnumber;
    /**
     * 用户状态 0待审核 1正常 2冻结
     */
    private Integer userStatus;
    /**
     * 第三方OA系统唯一标识
     */
    private String thirdOaUniqueFlag;


    /**
     * 角色权限范围all（全企业数据）include_subordinate(所在组织架构包含下级) personal （仅自己的数据）
     */
    private String roleAuth;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 职位类型：store_outside-店外，store_inside-店内
     */
    private String positionType;

    private List<DeptForUserDTO> deptList;
    /**
     * 默认区域
     */
    private List<RegionDO> defaultRegion;
    /**
     * 默认门店
     */
    private StoreDO defaultStore;

    /**
     * 是否需要完善用户信息
     */
    private Boolean isNeedImproveUserInfo;

}
