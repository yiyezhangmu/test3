package com.coolcollege.intelligent.model.enterprise;

import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.enterprise.UserSelectRangeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.google.common.base.Strings;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * @ClassName EnterpriseUserDO
 * @Description 用一句话描述什么
 * @author 首亮
 */
@Data
public class EnterpriseUserDO {
    /**
     * 用户主键id
     */
    private String id;

    /**
     * 钉钉用户id
     */
    private String userId;

    private String name;

    /**
     * 分机号
     */
    private String tel;

    /**
     * 备注
     */
    private String remark;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 员工的电子邮箱
     */
    private String email;

    /**
     * 是否已经激活, true表示已激活, false表示未激活
     */
    private Boolean active;

    /**
     * 是否为企业的主管理员, true表示是, false表示不是
     */
    private Boolean mainAdmin;
    /**
     * 是否为企业的管理员, true表示是, false表示不是
     */
    private Boolean isAdmin;

    /**
     * 在当前isv全局范围内唯一标识一个用户的身份,用户无法修改
     */
    private String unionid;

    /**
     * 是否号码隐藏, true表示隐藏, false表示不隐藏
     */
    private Boolean isHide;

    private String position;

    /**
     * 头像url
     */
    private String avatar;

    private Boolean isEnterprise;

    private String roles;

    /**
     * 是否是部门的主管, true表示是, false表示不是
     */
    private Boolean isLeader;

    /**
     * 用户创建时间
     */
    private Date createTime;

    /**
     * 用户语言环境:en_us/英语_美国,zh_cn/中文_简体,zh_hk/中文_繁体_HK
     */
    private String language;

    private String isLeaderInDepts;

    /**
     * 工号
     */
    private String jobnumber;

    /**
     * 人脸路径
     */
    private String faceUrl;

    private String appType;

    /**
     * 部门全路径
     */
    private String departments;

    private String password;

    private Integer userStatus;
    /**
     * 第三方OA系统唯一标识
     */
    private String thirdOaUniqueFlag;

    private Boolean subordinateChange;

    private String userRegionIds;

    private Integer userType;

    /**
     * 管辖用户范围：self-仅自己，all-全部人员，define-自定义
     */
    private String subordinateRange;

    /**
     * 创建人id
     */
    private String createUserId;

    /**
     * 加盟系统唯一标识
     */
    private String franchiseUniqueCode;


    public String getThirdOaUniqueFlag() {
        if(StringUtils.isBlank(thirdOaUniqueFlag)){
           return  null;
        }
        return thirdOaUniqueFlag;
    }

    /**
     * 设置成员名称
     *
     * @param name 成员名称
     */
    public void setName(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            this.name = name.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "");
        }else {
            this.name=name;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        EnterpriseUserDO that = (EnterpriseUserDO) o;
        return Objects.equals(unionid, that.unionid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(unionid);
    }


    public static EnterpriseUserDO getAiUser() {
        EnterpriseUserDO dingEnterpriseUserDTO =  new EnterpriseUserDO();
        dingEnterpriseUserDTO.setId(AIEnum.AI_ID.getCode());
        dingEnterpriseUserDTO.setName(AIEnum.AI_NAME.getCode());
        dingEnterpriseUserDTO.setUserId(AIEnum.AI_USERID.getCode());
        dingEnterpriseUserDTO.setMobile(AIEnum.AI_MOBILE.getCode());
        dingEnterpriseUserDTO.setRoles(AIEnum.AI_ROLES.getCode());
        dingEnterpriseUserDTO.setUnionid(AIEnum.AI_UUID.getCode());
        dingEnterpriseUserDTO.setActive(Boolean.TRUE);
        dingEnterpriseUserDTO.setMainAdmin(Boolean.TRUE);
        dingEnterpriseUserDTO.setIsAdmin(Boolean.TRUE);
        dingEnterpriseUserDTO.setSubordinateRange(UserSelectRangeEnum.ALL.getCode());
        dingEnterpriseUserDTO.setUserStatus(UserStatusEnum.NORMAL.getCode());
        return dingEnterpriseUserDTO;
    }



}
