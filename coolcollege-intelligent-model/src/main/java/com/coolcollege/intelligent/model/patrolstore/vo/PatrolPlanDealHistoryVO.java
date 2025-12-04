package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.common.enums.patrol.PatrolPlanStatusEnum;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.patrolstore.entity.TbPatrolPlanDealHistoryDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

/**
 * @author zhangchenbiao
 * @FileName: PatrolPlanPageVO
 * @Description:
 * @date 2024-09-04 11:37
 */
@Data
public class PatrolPlanDealHistoryVO {

    @ApiModelProperty("计划id")
    private Long planId;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("1通过 2拒绝")
    private Integer status;

    @ApiModelProperty("处理人")
    private String dealUserName;

    @ApiModelProperty("处理人头像")
    private String dealUserAvatar;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    public static List<PatrolPlanDealHistoryVO> convertList(List<TbPatrolPlanDealHistoryDO> processHistoryList, Map<String, EnterpriseUserDO> userMap){
        if(CollectionUtils.isEmpty(processHistoryList)){
            return Lists.newArrayList();
        }
        List<PatrolPlanDealHistoryVO> resultList = new ArrayList<>();
        for (TbPatrolPlanDealHistoryDO dealHistory : processHistoryList) {
            PatrolPlanDealHistoryVO result = new PatrolPlanDealHistoryVO();
            result.setPlanId(dealHistory.getPlanId());
            result.setNodeName(dealHistory.getNodeName());
            result.setStatus(dealHistory.getStatus());
            EnterpriseUserDO enterpriseUser= MapUtils.emptyIfNull(userMap).get(dealHistory.getHandleUserId());
            if(Objects.nonNull(enterpriseUser)){
                result.setDealUserName(enterpriseUser.getName());
                result.setDealUserAvatar(enterpriseUser.getAvatar());
            }
            result.setRemark(dealHistory.getRemark());
            result.setCreateTime(dealHistory.getCreateTime());
            resultList.add(result);
        }
        return resultList;
    }

}
