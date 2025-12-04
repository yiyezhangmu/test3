package com.coolcollege.intelligent.model.openApi.request;

import lombok.Data;

/**
 * @author chenyupeng
 * @since 2022/3/30
 */
@Data
public class OpenApiGetWorkOrderRequest {

    private Integer pageNumber;

    private Integer pageSize;

    private String storeId;

}
