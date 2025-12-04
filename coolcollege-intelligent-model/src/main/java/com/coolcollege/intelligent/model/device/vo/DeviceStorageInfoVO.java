package com.coolcollege.intelligent.model.device.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 设备存储信息VO
 * </p>
 *
 * @author wangff
 * @since 2025/8/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceStorageInfoVO {
    @ApiModelProperty("总大小，单位m")
    private Long totalSize;

    @ApiModelProperty("可用大小")
    private Long availableSize;

    @ApiModelProperty("类型，0读写、1只读、3备份、4快照")
    private Integer type;

    @ApiModelProperty("状态")
    private String status;
}
