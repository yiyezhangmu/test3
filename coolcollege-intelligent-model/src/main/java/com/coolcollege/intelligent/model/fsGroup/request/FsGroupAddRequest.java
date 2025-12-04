package com.coolcollege.intelligent.model.fsGroup.request;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupAddRequest {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("群名")
    private String name;

    @ApiModelProperty("群类型 store,region,other")
    private String type;

    @ApiModelProperty("群主id")
    private String groupOwnerId;

    @ApiModelProperty("群主姓名")
    private String groupOwnerName;

    @ApiModelProperty("绑定部门,regionIds")
    private List<StoreWorkCommonDTO> regionIds;

    @ApiModelProperty("配置群应用ids")
    private List<Long> sceneIds;

    @ApiModelProperty("群菜单ids")
    private List<Long> menuIds;

    @ApiModelProperty("群置顶ids")
    private List<Long> topMenuIds;


}
