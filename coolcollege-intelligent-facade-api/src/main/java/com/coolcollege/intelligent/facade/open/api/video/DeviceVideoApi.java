package com.coolcollege.intelligent.facade.open.api.video;

import com.coolcollege.intelligent.facade.dto.openApi.OpenApiDeviceStoreDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiDeviceStoreQueryDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiVideoDTO;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * @Author: hu hu
 * @Date: 2025/1/8 17:34
 * @Description:
 */
public interface DeviceVideoApi {

    OpenApiResponseVO getDeviceStore(OpenApiDeviceStoreDTO param);


    OpenApiResponseVO getVideoInfo(OpenApiVideoDTO param);

    OpenApiResponseVO getPastVideoInfo(OpenApiVideoDTO param);

    /**
     * 根据门店第三方唯一id查询门店下的设备列表
     * @param param 门店设备查询DTO
     */
    OpenApiResponseVO getDeviceByStoreThirdDeptId(OpenApiDeviceStoreQueryDTO param);

    /**
     * 获取直播流，仅校验设备id和通道号
     * @param param 入参
     */
    OpenApiResponseVO getVideoLive(OpenApiVideoDTO param);
}
