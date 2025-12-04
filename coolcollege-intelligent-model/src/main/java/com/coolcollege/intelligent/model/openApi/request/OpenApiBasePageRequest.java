package com.coolcollege.intelligent.model.openApi.request;

import lombok.Data;

/**
 * @author chenyupeng
 * @since 2022/4/28
 */
@Data
public class OpenApiBasePageRequest {

    private Integer pageNum=1;

    private Integer pageSize=10;
}
