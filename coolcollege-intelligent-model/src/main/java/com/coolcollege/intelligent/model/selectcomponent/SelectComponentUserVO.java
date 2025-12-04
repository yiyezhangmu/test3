package com.coolcollege.intelligent.model.selectcomponent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @desc: 选人组件中人员信息的返回
 * @author: xuanfeng
 * @date: 2021-10-27 15:07
 */
@Data
public class SelectComponentUserVO {
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
