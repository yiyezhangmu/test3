package com.coolcollege.intelligent.model.fsGroup.request;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupTopMenuRequest {

    @ApiModelProperty("主键")
    private Long id;

    /**
     * 置顶消息名
     */
    @ApiModelProperty("顶部菜单名")
    @NotNull(message = "顶部菜单名不能为空")
    private String topName;
    /**
     * 0:自定义链接1:系统地址
     */
    @ApiModelProperty("0:自定义链接1:系统地址")
    private Integer urlType;
    /**
     * 自定义链接地址
     */
    @ApiModelProperty("链接地址")
    @NotNull(message = "链接地址不能为空")
    private String url;
    /**
     * 系统地址code
     */
    @ApiModelProperty("系统地址code")
    private String urlCode;
    /**
     * 发送门店 null为发送所有门店
     */
    @ApiModelProperty("发送门店 不传为发送所有门店")
    private List<StoreWorkCommonDTO> sendStoreRegionIds;
    /**
     * 同上
     */
    @ApiModelProperty("区域群列表")
    private String sendRegionChatIds;
    /**
     * 同上
     */
    @ApiModelProperty("其他群列表")
    private String sendOtherChatIds;

}
