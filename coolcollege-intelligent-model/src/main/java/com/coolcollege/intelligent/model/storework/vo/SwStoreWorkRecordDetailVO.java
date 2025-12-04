package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.storework.dto.SwStoreWorkTableDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/27 16:23
 * @Version 1.0
 */
@Data
public class SwStoreWorkRecordDetailVO {


    private String storeId;

    private String storeName;

    private String storeNum;

    private BigDecimal score;

    private List<String> regionNameList;

    private String storeWorkDate;

    private List<SwStoreWorkTableDTO>  swStoreWorkTableDTOS;

}
