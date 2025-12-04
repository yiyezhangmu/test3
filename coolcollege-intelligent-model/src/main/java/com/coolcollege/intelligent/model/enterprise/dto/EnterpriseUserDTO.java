package com.coolcollege.intelligent.model.enterprise.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.MySubordinatesDTO;
import com.coolcollege.intelligent.model.usergroup.dto.UserGroupDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName EnterpriseUserDTO
 * @Description 用一句话描述什么
 * @author 首亮
 */
@Data
public class EnterpriseUserDTO {

    private String id;
    /**
     * 员工UserID
     */
    @Excel(name = "员工UserID", width = 20, orderNum = "2")
    private String userId;
    /**
     * 姓名
     */
    @Excel(name = "姓名", width = 20, orderNum = "3")
    private String name;
    /**
     * 工号
     */
    @Excel(name = "工号", width = 20, orderNum = "4")
    private String jobnumber;
    private String department;
    private String departments;
    private List<String> departmentNames;
    private List<String> positionNames;
    private String unionid;
    private Date createTime;
    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 20, orderNum = "7")
    private String mobile;
    private String faceUrl;
    private Boolean active;
    /**
     * 角色
     */
    @Excel(name="角色",width = 30,orderNum ="9")
    private String role;
    private String roles;
    @Excel(name="门店数",width = 20,orderNum = "8")
    private Integer storeNum;

    private String email;
    private String remark;
    private String position;
    private String avatar;
    private String roleName;
    private String roleAuth;
    private String roleAuthName;
    private Boolean isAdmin;
    private String deptId;
    private List<AuthRegionStoreUserDTO>  authRegionStoreList;
    private Integer storeCount;
    /**
     * 用户状态
     */
    private Integer userStatus;
    /**
     * 用户人事状态
     */
    private String userPersonnelStatus;

    /**
     * 第三方OA系统唯一标识
     */
    private String thirdOaUniqueFlag;

    /**
     * 用户分组
     */
    private List<UserGroupDTO> userGroupList;
    /**
     * 管辖用户范围
     */
    private String subordinateUserRange;

    /**
     * auto自动关联 select手动选择
     */
    private List<String> sourceList;

    /**
     * 我的下属集合
     */
    private List<MySubordinatesDTO> mySubordinates;

    @ApiModelProperty("选取权限 true可选 false不可选")
    private Boolean selectFlag;

    @ApiModelProperty("用户类型 0内部员工 1外部员工")
    private Integer userType;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人")
    private String createUserName;
}
