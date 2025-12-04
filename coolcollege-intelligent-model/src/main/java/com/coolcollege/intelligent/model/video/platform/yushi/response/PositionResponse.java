package com.coolcollege.intelligent.model.video.platform.yushi.response;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/19
 */
@Data
public class PositionResponse {

    /**
     * 索引位
     */
    private Integer presetIndex;

    /**
     * 预置位名称
     */
    private String name;

    /**
     * 预置位图片
     */
    private String image;
}
