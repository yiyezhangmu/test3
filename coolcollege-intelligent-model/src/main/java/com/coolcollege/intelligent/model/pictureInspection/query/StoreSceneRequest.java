package com.coolcollege.intelligent.model.pictureInspection.query;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * @Author suzhuhong
 * @Date 2021/9/1 11:17
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSceneRequest {

    private Long id;

    private String name;

    private String sceneType;

    private String remark;


}
