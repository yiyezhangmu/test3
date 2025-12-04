package com.coolcollege.intelligent.model.video.platform.imou.request;

import lombok.Data;

import java.util.Map;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/24
 */
@Data
public class ImouBaseRequest {
    private String id;
    private ImouSystemDTO system;
    private Map<String,Object> params;
}
