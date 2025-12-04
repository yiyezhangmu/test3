package com.coolcollege.intelligent.model.activity.vo;

import com.coolcollege.intelligent.model.activity.entity.ActivityViewRangeDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: ViewRangeDTO
 * @Description:
 * @date 2023-07-05 20:08
 */
@Data
public class ViewRangeVO {

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

    public static List<ViewRangeVO> convertVO(List<ActivityViewRangeDO> viewRangeList, Map<Long, String> regionNameMap, Map<String, String> userNameMap){
        List<ViewRangeVO> resultList = new ArrayList<>();
        for (ActivityViewRangeDO viewRange : viewRangeList) {
            ViewRangeVO view = new ViewRangeVO();
            if("personal".equals(viewRange.getNodeType())){
                view.setPersonalId(viewRange.getPersonalId());
                view.setPersonalName(userNameMap.get(viewRange.getPersonalId()));
            }
            if("region".equals(viewRange.getNodeType())){
                view.setRegionId(viewRange.getRegionId());
                view.setRegionName(regionNameMap.get(Long.valueOf(viewRange.getRegionId())));
            }
            view.setNodeType(viewRange.getNodeType());
            resultList.add(view);
        }
        return resultList;
    }

}
