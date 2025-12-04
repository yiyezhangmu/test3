package com.coolcollege.intelligent.model.openApi.request;

import lombok.Data;

/**
 * @author chenyupeng
 * @since 2022/3/29
 */
@Data
public class XfsgOpenApiRequest {

    private String sign;

    private Long timestamp;

    private String bizContent;
}
