package com.coolcollege.intelligent.model.passengerflow.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/20
 */
@Data
public class PassengerStoreRankRequest extends PassengerBaseRequest {

    private String storeIdStr;
    private Long regionId;
    @NotNull(message = "场景不能为空")
    private Long sceneId;
    private Integer pageNo=1;
    private Integer pageSize=10;

}
