package com.coolcollege.intelligent.model.openApi.request;

import lombok.Data;

/**
 * @author chenyupeng
 * @since 2022/3/29
 */
@Data
public class OpenApiRequest {

    private String sign;

    private String userId;

    private String bizContent;
}
