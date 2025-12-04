package com.coolcollege.intelligent.model.passengerflow.request;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/19
 */
@Data
public class PassengerSceneHourDayRequest extends PassengerBaseRequest {
    private String storeIdStr;
    private Long regionId;
    private Long sceneId;
}
