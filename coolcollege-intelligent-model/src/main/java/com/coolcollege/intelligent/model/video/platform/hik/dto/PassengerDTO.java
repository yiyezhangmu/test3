package com.coolcollege.intelligent.model.video.platform.hik.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/2/13 13:59
 * @Version 1.0
 */
@Data
public class PassengerDTO {


        private String dateTime;

        private Integer passengerInCount;

        private Integer passengerPassCount;

        private Integer passengerOutCount;

}
