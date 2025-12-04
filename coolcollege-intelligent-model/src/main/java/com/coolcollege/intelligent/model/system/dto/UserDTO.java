package com.coolcollege.intelligent.model.system.dto;

import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentStoreVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentUserRoleVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/12/08
 */
@Data
public class UserDTO {
    private String userId;
    private String userName;
    /**
     * 工号
     */
    private String jobNumber;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户头像
     */
    private String faceUrl;
    /**
     * 职位信息
     */
    private SelectComponentUserRoleVO positionInfo;

    /**
     * 人员区域信息
     */
    private List<SelectComponentRegionVO> regionVos;

    /**
     * 人员门店信息
     */
    private List<SelectComponentStoreVO> storeVos;

    @ApiModelProperty("选取权限 true可选 false不可选")
    private Boolean selectFlag;
}
