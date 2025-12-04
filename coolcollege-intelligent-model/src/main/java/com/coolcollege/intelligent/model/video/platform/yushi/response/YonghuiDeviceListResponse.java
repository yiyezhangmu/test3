package com.coolcollege.intelligent.model.video.platform.yushi.response;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/24
 */
@Data
public class YonghuiDeviceListResponse {
    private Integer total;
    private List<YonghuiDeviceResponse> deviceList;
}
