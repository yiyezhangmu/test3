package com.coolcollege.intelligent.model.pictureInspection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author suzhuhong
 * @Date 2021/8/26 17:39
 * @Version 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StoreSceneVO {


    /**
     * id
     */
    private Long id;

    /**
     * 设备场景名称
     */
    private String name;
}
