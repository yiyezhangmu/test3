package com.coolcollege.intelligent.model.region.dto;

import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentStoreVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentUserRoleVO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/11/30 17:13
 */
@Data
public class RegionChildDTO {

    private String id;

    private String pid;

    private String name;

    private String path;

    private boolean hasChild = false;

    private String address;

    private boolean storeFlag;

    private Boolean  personalFlag;

    private Boolean unclassifiedFlag;

    private boolean hasAuth;

    private Long storeCount = 0L;

    private Long userCount = 0L;

    private String corpId;

    private String storeId;

    private String regionPath;

    private String regionType;

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

//    private List<StoreAreaDTO> stores;

    @ApiModelProperty("选取权限 true可选 false不可选")
    private Boolean selectFlag;

    private String synDingDeptId;

    /**
     * 门店状态（open：营业、closed：闭店、not_open：未开业）
     */
    private String storeStatus;
}
