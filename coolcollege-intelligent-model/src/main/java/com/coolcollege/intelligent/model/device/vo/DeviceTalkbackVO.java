package com.coolcollege.intelligent.model.device.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 设备对讲VO
 * </p>
 *
 * @author wangff
 * @since 2025/8/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTalkbackVO {
    @ApiModelProperty("对讲地址")
    private String url;
}
