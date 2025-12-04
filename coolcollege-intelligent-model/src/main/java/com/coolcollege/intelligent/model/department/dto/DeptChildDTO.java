package com.coolcollege.intelligent.model.department.dto;

import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentStoreVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentUserRoleVO;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/12/9 16:29
 */
@Data
public class DeptChildDTO {

    private String id;

    private String parentId;

    private String name;

    private String avatar;

    private Integer userNum;

    private Boolean hasChild;

    private Boolean userFlag;
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

    /**
     * 部门次序
     */
    private Integer departOrder;
}
