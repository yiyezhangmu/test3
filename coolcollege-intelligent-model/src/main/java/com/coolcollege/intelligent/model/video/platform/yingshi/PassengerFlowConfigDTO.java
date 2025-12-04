package com.coolcollege.intelligent.model.video.platform.yingshi;

import lombok.Data;

import java.util.Map;

/**
 * describe:客流统计配置信息
 * example:
 * {
 *         "line": {
 *             "x1": "0.5000000",
 *             "y1": "0.0000000",
 *             "x2": "0.5000000",
 *             "y2": "1.0000000"
 *         },
 *         "direction": {
 *             "x1": "0.5000000",
 *             "y1": "0.5000000",
 *             "x2": "0.2500000",
 *             "y2": "0.5000000"
 *         }
 *     }
 * @author zhouyiping
 * @date 2021/10/08
 */
@Data
public class PassengerFlowConfigDTO {
    /**
     * 统计线的两个坐标点，坐标范围为0到1之间的7位浮点数，(0,0)坐标在左上角，格式如{"x1": "0.0","y1": "0.5","x2": "1","y2": "0.5"}
     */
    private LinearCoordinatesDTO line;
    /**
     * 指示方向的两个坐标点，(x1,y1)为起始点，(x2,y2)为结束点格式如{"x1": "0.5","y1": "0.5","x2": "0.5","y2": "0.6"}，与统计线保持垂直
     */
    private LinearCoordinatesDTO direction;

}
