package com.coolcollege.intelligent.model.user;

import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.MySubordinatesDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: UserAddDTO
 * @Description:用户新增
 * @date 2021-07-20 15:57
 */
@Data
public class UserAddDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String name;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 工号
     */
    private String jobnumber;

    /**
     * 备注
     */
    private String remark;

    /**
     * 用户状态
     */
    private Integer userStatus;


    private String roleId;


    private List<AuthRegionStoreUserDTO> authRegionStoreList;

    /**
     * 第三方OA系统唯一标识
     */
    private String thirdOaUniqueFlag;


    @ApiModelProperty("分组id")
    private List<String> groupIdList;

    @ApiModelProperty("管辖用户范围：self-仅自己，all-全部人员，define-自定义")
    private String subordinateUserRange;

    @ApiModelProperty("管辖用户")
    private List<MySubordinatesDTO> mySubordinates;

    @ApiModelProperty("auto自动关联 select手动选择")
    private List<String> sourceList;

    @ApiModelProperty("直属主管")
    private String directSuperiorId;

    @ApiModelProperty("部门ids")
    private List<String> regionIds;

    @Max(1)
    @Min(0)
    @ApiModelProperty("用户类型 用户类型 0内部员工 1外部员工")
    private Integer userType;

}
