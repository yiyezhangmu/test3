package com.coolcollege.intelligent.model.passengerflow.request;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/21
 */
@Data
public class PassengerAchievementRequest extends PassengerBaseRequest {

    private Long regionId;
    private String storeIdStr;
    private Integer pageNo=1;
    private Integer pageSize=10;
}
