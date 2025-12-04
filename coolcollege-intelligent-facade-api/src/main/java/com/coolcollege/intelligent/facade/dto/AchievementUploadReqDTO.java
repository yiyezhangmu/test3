package com.coolcollege.intelligent.facade.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 业绩上传远程服务请求参数
 * @Author: mao
 * @CreateDate: 2021/6/11 14:03
 */
@Data
public class AchievementUploadReqDTO {

    /**
     * 业绩产生时间
     */
    private Date produceTime;
    /**
     * 业绩产生人id
     */
    private String produceUserId;
    /**
     * 业绩产生人姓名
     */
    private String produceUserName;
    /**
     * 业绩值
     */
    private BigDecimal achievementAmount;
    /**
     * 业绩类型名称
     */
    private String achievementTypeName;
    /**
     * 门店数据
     */
    private StoreRemoteDTO store;


}
