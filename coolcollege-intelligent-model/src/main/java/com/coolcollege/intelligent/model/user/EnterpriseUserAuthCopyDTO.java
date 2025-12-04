package com.coolcollege.intelligent.model.user;

import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 复制用户区域门店权限对象
 * @author ：xugangkun
 * @description：TODO
 * @date ：2021/10/12 15:28
 */
@Data
public class EnterpriseUserAuthCopyDTO {

    /**
     * 用户id列表
     */
    @NotEmpty(message = "用户id列表不能为空")
    private List<String> userIds;

    /**
     * 权限信息列表
     */
    private List<AuthRegionStoreUserDTO> authRegionStoreList;

    /**
     * 复制权限方式
     */
    @NotNull(message = "复制权限方式不能为空")
    private Boolean isCover;
}
