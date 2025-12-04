package com.coolcollege.intelligent.model.device.response.tplink;

import lombok.Data;

import java.util.List;

@Data
public class TPLocalFileResponse {

    private long startTime;

    private long endTime;

    private List<Integer> videoType;

    private Integer sourceType;

}
