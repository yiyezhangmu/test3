package com.coolcollege.intelligent.model.device.vo;

import com.coolstore.base.enums.YunTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: DeviceYunTypeVO
 * @Description:
 * @date 2022-12-16 15:20
 */
@Data
public class DeviceYunTypeVO {

    @ApiModelProperty("云类型")
    private YunTypeEnum yunType;

    @ApiModelProperty("最近同步时间")
    private String lastSyncTime;

    @ApiModelProperty("同步状态 0同步中 1同步成功 2同步失败")
    private Integer syncStatus;

    @ApiModelProperty("视频云端访问id")
    private String accessKeyId;

    @ApiModelProperty("视频云端访问密钥")
    private String accessSecret;

}
