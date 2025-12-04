package com.coolcollege.intelligent.model.metatable.dto;

import lombok.Data;

import java.util.List;

/**
 * @author chenyupeng
 * @since 2022/4/1
 */
@Data
public class DetectWorkWearDataDTO {

    /**
     * 数据采集表id
     */
    private Long dataStaId;

    /**
     * 场景id
     */
    private Long storeSceneId;

    List<DetectWorkWearAnalyseDTO> detectWorkWearAnalyseList;
}
