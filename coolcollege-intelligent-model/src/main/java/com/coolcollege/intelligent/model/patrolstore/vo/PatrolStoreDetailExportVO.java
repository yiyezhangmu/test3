package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.store.vo.ExtendFieldInfoVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/6 13:55
 * @Version 1.0
 */
@Data
public class PatrolStoreDetailExportVO {

    private Integer index;

    private String patrolStoreUserId;

    private String  patrolStoreUserName;

    private String  patrolStoreUserRoleName;

    private String storeId;

    private String storeName;

    private String storeNum;

    private Long regionId;

    private String regionName;

    private String storeAddress;

    private Date patrolStoreDate;

    private BigDecimal storeTotalScore;

    private String storeTotalScoreStr;

    private String storeScoreRate;

    private Integer storeScoreRateRank;

    private String passRate;

    private String failureRate;

    private String patrolStoreDuration;

    private Date signInTime;

    private Date signOutTime;

    private String patrolStoreResult;

    private BigDecimal checkAwardPunish;

    private List<String> regionNameList;

    private List<Long> businessIds;

    private Long businessId;

    private List<ExtendFieldInfoVO> storeExtendField;

}
