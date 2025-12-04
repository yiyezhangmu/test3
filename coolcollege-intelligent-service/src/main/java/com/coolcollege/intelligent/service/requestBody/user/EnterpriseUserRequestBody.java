package com.coolcollege.intelligent.service.requestBody.user;

import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.MySubordinatesDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName EnterpriseUserRequestBody
 * @Description 用户请求体
 */
@Data
public class EnterpriseUserRequestBody {

    @JsonProperty("role_id")
    @NotBlank(message = "角色ID不能为空")
    private String roleId;

    @JsonProperty("user_id")
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @JsonProperty("user_name")
    @NotBlank(message = "用户名不能为空")
    private String userName;
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
     * 头像url
     */
//    @JsonProperty("face_url")
    private String faceUrl;
    /**
     * 工号
     */
    private String jobnumber;

    /**
     * 用户状态 0待审核 1正常 2冻结
     */
    private Integer userStatus;

    @JsonProperty("auth_region_store_list")
    private List<AuthRegionStoreUserDTO> authRegionStoreList;

    /**
     * 第三方OA系统唯一标识
     */
    private String thirdOaUniqueFlag;

    /**
     * 部门id
     */
    List<String> regionIds = new ArrayList<>();

    /**
     * 直属上级ID
     */
    String directSuperiorId;

    /**
     * 我的下属
     */
    List<MySubordinatesDTO> mySubordinates;

    /**
     * 管辖用户范围：self-仅自己，all-全部人员，define-自定义
     */
    private String subordinateUserRange;

    /**
     * auto自动关联 select手动选择
     */
    private List<String> sourceList;

    /**
     * 用户分组
     */
    private List<String> groupIdList;


}
