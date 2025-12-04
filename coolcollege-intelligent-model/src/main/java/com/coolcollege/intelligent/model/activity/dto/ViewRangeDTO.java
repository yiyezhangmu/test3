package com.coolcollege.intelligent.model.activity.dto;

import com.coolcollege.intelligent.model.activity.entity.ActivityViewRangeDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: ViewRangeDTO
 * @Description:
 * @date 2023-07-05 20:08
 */
@Data
public class ViewRangeDTO {

    @ApiModelProperty("区域id")
    private String regionId;

    @ApiModelProperty("区域名称")
    private String regionName;

    @ApiModelProperty("人员id")
    private String personalId;

    @ApiModelProperty("人员名称")
    private String personalName;

    @ApiModelProperty("节点类型 人:personal;部门:region")
    private String nodeType;

    public static List<ActivityViewRangeDO> convertDO(Long activityId, String userId, List<ViewRangeDTO> rangeList){
        if(CollectionUtils.isEmpty(rangeList)){
            return Lists.newArrayList();
        }
        List<ActivityViewRangeDO> resultList = new ArrayList<>();
        for (ViewRangeDTO viewRange : rangeList) {
            ActivityViewRangeDO view = new ActivityViewRangeDO();
            view.setActivityId(activityId);
            view.setRegionId(viewRange.getRegionId());
            view.setPersonalId(viewRange.getPersonalId());
            view.setNodeType(viewRange.getNodeType());
            view.setCreateUserId(userId);
            view.setUpdateUserId(userId);
            view.setCreateTime(new Date());
            view.setUpdateTime(new Date());
            resultList.add(view);
        }
        return resultList;
    }

}
