package com.coolcollege.intelligent.model.passengerflow.vo;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/18
 */
@Data
public class PassengerDeviceHourVO {
    private Integer hour;
    private List<PassengerDeviceHourDayVO> passengerDeviceHourDayVOList;
}
