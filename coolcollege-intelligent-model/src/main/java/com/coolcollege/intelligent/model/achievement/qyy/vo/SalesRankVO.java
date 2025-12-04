package com.coolcollege.intelligent.model.achievement.qyy.vo;


import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: SalesRankVO
 * @Description: 业绩排行(区域、门店)
 * @date 2023-04-04 16:12
 */
@Data
public class SalesRankVO extends DataUploadVO{

    @ApiModelProperty("排行")
    private List<SalesReportVO> rankList;

    public static SalesRankVO convert(List<AchieveQyyRegionDataDO> rankList, NodeTypeEnum nodeType){
        SalesRankVO result = new SalesRankVO();
        List<SalesReportVO> salesRankList = new ArrayList<>();
        Date lastUpdateTime = null;
        for (AchieveQyyRegionDataDO achieveQyyRegion : rankList) {
            SalesReportVO sale = SalesReportVO.convert(achieveQyyRegion, nodeType);
            if(Objects.isNull(lastUpdateTime)){
                lastUpdateTime = achieveQyyRegion.getEtlTm();
            }
            if(achieveQyyRegion.getEtlTm().after(lastUpdateTime)){
                lastUpdateTime = achieveQyyRegion.getEtlTm();
            }
            salesRankList.add(sale);
        }
        result.setRankList(salesRankList);
        if(Objects.nonNull(lastUpdateTime)){
            result.setEtlTm(DateUtil.format(lastUpdateTime, "yyyy-MM-dd HH:mm"));
        }
        return result;
    }

}
