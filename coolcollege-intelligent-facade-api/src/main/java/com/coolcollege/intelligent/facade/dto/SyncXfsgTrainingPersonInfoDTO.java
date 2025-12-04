package com.coolcollege.intelligent.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 鲜丰门店下培训人员拉取
 * @author chenyupeng
 * @since 2021/8/17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncXfsgTrainingPersonInfoDTO {

    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 工号
     */
    private String jobnumber;
    /**
     * 身份证号码
     */
    private String idCard;
    /**
     * 门店编号
     */
    private String storeNum;


}
