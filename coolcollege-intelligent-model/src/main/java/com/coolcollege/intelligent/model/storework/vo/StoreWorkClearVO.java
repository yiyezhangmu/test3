package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/9/21 18:57
 * @Version 1.0
 */
@Data
@ApiModel(value = "店务日清VO")
public class StoreWorkClearVO {

    private Integer timeUnion;

    private Boolean isFinish;

}
