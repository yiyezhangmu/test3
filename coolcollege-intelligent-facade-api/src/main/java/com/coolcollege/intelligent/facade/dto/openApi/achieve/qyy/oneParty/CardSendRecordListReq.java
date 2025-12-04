package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.oneParty;

import lombok.Data;

import java.util.List;

@Data
public class CardSendRecordListReq {
    /**
     * 场景卡片名称
     */
    private String sceneCardName;

    /**
     * 开始时间
     */
    private Long beginTime;

    /**
     * 结束时间
     */
    private Long endTime;

    private List<Long> ids;

    private Integer pageNum;

    private Integer pageSize;



}
