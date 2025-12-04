package com.coolcollege.intelligent.model.passengerflow.vo;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/21
 */
@Data
public class PassengerAchievementVO {
    /**
     * 日期
     */
    private Date flowDay;

    /**
     * 进店
     */
    private Integer flowInCount;

    /**
     * 店外客流
     */
    private Integer flowInOutCount;

    /**
     * 进店率
     */
    private Double flowInPercent;

    /**
     * 过店客流
     */
    private Integer flowPassCount;


}
