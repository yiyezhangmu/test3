package com.coolcollege.intelligent.model.homeTemplate.DTO;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/7/7 10:10
 * @Version 1.0
 */
@ApiModel
@Data
public class CommonFunctionsDTO {

    /**
     * PC 个人常用功能
     */
    @ApiModelProperty("用户常用功能")
    String commonFunctions;
}
