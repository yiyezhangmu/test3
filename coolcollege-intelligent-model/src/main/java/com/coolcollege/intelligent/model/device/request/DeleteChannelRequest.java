package com.coolcollege.intelligent.model.device.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/18
 */
@Data
public class DeleteChannelRequest {

    @Valid
    @NotEmpty(message = "通道列表不能为空")
    @ApiModelProperty(value = "通道列表")
    private List<ChannelDelete> channelList;

    @Data
    public static class ChannelDelete {

        @NotBlank(message = "设备id不能为空")
        private String parentDeviceId;

        @NotBlank(message = "通道号不能为空")
        private String channelNo;
    }
}
