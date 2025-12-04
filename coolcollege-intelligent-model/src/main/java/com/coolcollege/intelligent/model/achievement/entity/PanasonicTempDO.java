package com.coolcollege.intelligent.model.achievement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PanasonicTempDO {
    private Long id;

    private String storeId;
    private String category;
    private String middleClass;
    private String type;
    private String mainClass;
    private Integer goodNum;
    private Integer offOnStatus;
    private Date offTime;
    private Date onTime;
    private String group;
}
