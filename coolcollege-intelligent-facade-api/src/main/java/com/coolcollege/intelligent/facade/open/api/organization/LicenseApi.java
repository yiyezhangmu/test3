package com.coolcollege.intelligent.facade.open.api.organization;

import com.coolcollege.intelligent.facade.dto.openApi.OpenApiStoreLicenseRequest;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/25 9:54
 */

public interface LicenseApi {
    /**
     * 添加证照
     */
    OpenApiResponseVO addLicense(OpenApiStoreLicenseRequest requests);

    /**
     * 获取证照类型详情
     */
    OpenApiResponseVO getLicense();

}
