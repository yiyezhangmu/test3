package com.coolcollege.intelligent.model.device.request;

import com.coolcollege.intelligent.model.openApi.request.OpenApiBasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OpenDevicePageRequest extends OpenApiBasePageRequest {

    @ApiModelProperty("第三方门店id")
    private String thirdStoreId;

}
