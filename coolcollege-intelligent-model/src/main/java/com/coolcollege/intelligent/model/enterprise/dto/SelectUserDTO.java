package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentStoreVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentUserRoleVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/11/18 15:02
 */
@Data
public class SelectUserDTO {

    private String userName;

    private String userId;

    private String avatar;
    /**
     * 用户工号
     */
    private String jobNumber;
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
