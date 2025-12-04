package com.coolcollege.intelligent.facade.dto.openApi.vo;

import com.coolcollege.intelligent.facade.dto.openApi.PersonBasicDTO;
import com.coolcollege.intelligent.facade.dto.openApi.TbQuestionDealRecordVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 问题工单详情vo
 * @author zhangnan
 * @date 2021-12-23 14:11
 */
@Data
public class QuestionRecordDetailVO {

    private Long id;

    private String taskName;

    private String taskInfo;

    private Long metaColumnId;

    private String metaColumnName;

    private String metaColumnLevel;

    private String metaColumnDescription;

    private BigDecimal supportScore;

    private BigDecimal checkScore;

    private BigDecimal rewardPenaltMoney;

    private String storeId;

    private String storeName;

    private String storeNum;

    private Long regionId;

    private String regionPath;

    private String regionName;

    private Date handlerEndTime;

    private String status;

    private Boolean isOverdue;

    private String handleUserId;

    private String handleUserName;

    private String createUserId;

    private String createUserName;

    private Date createTime;

    private String taskDesc;

    private Long unifyTaskId;

    private Integer createType;

    private Long dataColumnId;

    private Boolean learnFirst;

    private String approveUserId;

    private String approveUserName;

    private List<PersonBasicDTO> ccUsers;

    private List<PersonBasicDTO> handleUsers;

    private List<PersonBasicDTO> approveUsers;

    private List<PersonBasicDTO> secondApproveUsers;

    private List<PersonBasicDTO> thirdApproveUsers;

    private String attachUrl;

    private Long taskStoreId;

    private String secondApproveUserId;

    private String secondApproveUserName;

    private String thirdApproveUserId;

    private String thirdApproveUserName;

    private String checkResultReason;

    private List<TbQuestionDealRecordVO> dealList;
}
