package com.coolcollege.intelligent.model.wechat.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: huhu
 * @Date: 2024/9/25 17:46
 * @Description: 跳小程序所需数据
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MiniProgramRequest {

    @ApiModelProperty("小程序appid")
    private String appid;

    @ApiModelProperty("小程序的具体页面路径")
    private String pagepath;
}
