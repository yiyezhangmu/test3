package com.coolcollege.intelligent.facade.open.api.organization;

import com.coolcollege.intelligent.facade.dto.openApi.OpenApiAddRegionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiRegionAuthDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiRegionDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiRemoveRegionAuthDTO;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * @Author suzhuhong
 * @Date 2022/7/18 10:26
 * @Version 1.0
 */
public interface RegionApi {

    /**
     * 组织架构列表 分页 最多一次查询100条数据
     * @param openApiRegionDTO
     * @return
     */
    OpenApiResponseVO regionList(OpenApiRegionDTO openApiRegionDTO);


    /**
     * 区域详情
     * @param openApiRegionDTO
     * @return
     */
    OpenApiResponseVO regionDetail(OpenApiRegionDTO openApiRegionDTO);

    /**
     * 添加区域
     * @param openApiRegionDTO
     * @return
     */
    OpenApiResponseVO addRegion(OpenApiRegionDTO openApiRegionDTO);

    /**
     * 新增区域  parentId为第三方id
     * @param param
     * @return
     */
    OpenApiResponseVO insertOrUpdateRegion(OpenApiAddRegionDTO param);

    /**
     * 更新区域
     * @param openApiRegionDTO
     * @return
     */
    OpenApiResponseVO updateRegion(OpenApiRegionDTO openApiRegionDTO);

    /**
     * 删除区域
     * @param openApiRegionDTO
     * @return
     */
    OpenApiResponseVO deleteRegion(OpenApiRegionDTO openApiRegionDTO);

    /**
     * 查询人员区域权限
     * @param openApiRegionAuthDTO
     * @return
     */
    OpenApiResponseVO getRegionAuth(OpenApiRegionAuthDTO openApiRegionAuthDTO);

    /**
     * 移除用户权限
     * @param openApiRemoveRegionAuthDTO
     * @return
     */
    OpenApiResponseVO removeUserRegionAuth(OpenApiRemoveRegionAuthDTO openApiRemoveRegionAuthDTO);

    /**
     * 添加用户权限
     * @param openApiRemoveRegionAuthDTO
     * @return
     */
    OpenApiResponseVO addUserRegionAuth(OpenApiRemoveRegionAuthDTO openApiRemoveRegionAuthDTO);
}
