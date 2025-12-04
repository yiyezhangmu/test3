package com.coolcollege.intelligent.model.enterprise.vo;

import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.MySubordinatesDTO;
import com.coolcollege.intelligent.model.region.dto.RegionDTO;
import com.coolcollege.intelligent.model.usergroup.dto.UserGroupDTO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName EnterpriseDetailUserVO
 * @Description 返回用户详情信息
 * @author 首亮
 */
@Data
public class EnterpriseDetailUserVO {
    /**
     * 姓名
     */
    private String userId;
    private String userName;
    private String mobile;
    private String email;
    private String jobnumber;
    private String remark;
    private String faceUrl;
    /**
     * 用户拥有的角色
     */
    private Long roleId;
    private String roleName;
    private Integer userStatus;

    /**
     * 人员所在部门（部门即区域） 集合
     */
    private List<RegionDTO> regionBaseDataList;

    private List<EntUserRoleDTO> userRoles;

    /**
     * 角色下的权限列表
     */
    private List<AuthRegionStoreUserDTO> authRegionStoreList;

    /**
     * 第三方OA系统唯一标识
     */
    private String thirdOaUniqueFlag;

    /**
     * 直接上级ID
     */
    private String directSuperiorId;

    /**
     * 直接上级名称
     */
    private String directSuperiorName;

    /**
     * 直接上级来源 auto 同步 select手动选择
     */
    private String directSource;

    /**
     * 我的下属集合
     */
    private List<MySubordinatesDTO> mySubordinates;

    /**
     * 用户分组
     */
    private List<UserGroupDTO> userGroupList;

    /**
     * 管辖用户范围：self-仅自己，all-全部人员，define-自定义
     */
    private String subordinateUserRange;

    /**
     * auto自动关联 select手动选择
     */
    private List<String> sourceList;

    /**
     * 是否有密码
     */
    private Boolean hasPassword;

}
