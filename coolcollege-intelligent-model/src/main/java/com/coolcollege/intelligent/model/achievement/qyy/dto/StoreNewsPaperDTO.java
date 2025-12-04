package com.coolcollege.intelligent.model.achievement.qyy.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class  StoreNewsPaperDTO implements Serializable {
    private List<String> regionId;

    private String storeName;

    private String startTime;

    private String endTime;

    private Integer pageNum;

    private Integer pageSize;


}
