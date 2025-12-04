package com.coolcollege.intelligent.model.video.param;

import lombok.Data;

import java.util.List;

/**
 * @author chenyupeng
 * @since 2021/10/11
 */
@Data
public class SmallVideoSumParam {

    /**
     * 企业id
     */
    private String enterpriseId;

    private List<SmallVideoParam> smallVideoParam;
}
