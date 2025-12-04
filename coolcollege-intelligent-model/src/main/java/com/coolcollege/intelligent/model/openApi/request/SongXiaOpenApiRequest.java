package com.coolcollege.intelligent.model.openApi.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author chenyupeng
 * @since 2022/3/29
 */
@Data
public class SongXiaOpenApiRequest {

    private String sign;

    private Long timestamp;

    private JSONObject bizContent;

    private String enterpriseId;
}
