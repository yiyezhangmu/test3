package com.coolcollege.intelligent.model.aliyun;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邵凌志
 * @date 2020/8/26 15:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AliyunWeekTimeDTO {
    /**
     * 周几，1，2，3，4，5，6，7
     * 小时,00:00至23:00
     */
    private String time;
    /**
     * 本次数据
     */
    private Integer thisNum;
    /**
     * 历史数据
     */
    private Integer lastNum;
}
