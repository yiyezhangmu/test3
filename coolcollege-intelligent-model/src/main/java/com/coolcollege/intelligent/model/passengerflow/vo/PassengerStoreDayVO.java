package com.coolcollege.intelligent.model.passengerflow.vo;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/20
 */
@Data
public class PassengerStoreDayVO {
    private String storeId;
    private String storeName;
    private List<PassengerDeviceHourDayVO> passengerDeviceHourDayVOList;
}
