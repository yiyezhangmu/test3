package com.coolcollege.intelligent.model.video.platform.yingshi;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/08
 */
@Data
public class PassengerFlowHourlyDTO {

    private Integer inFlow;
    private Integer outFlow;
    private Integer hourIndex;
}
