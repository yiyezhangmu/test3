package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.NodeTypeEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.model.qyy.AchieveQyyRegionDataDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: FinishRateRankVO
 * @Description: 完成率排行
 * @date 2023-04-04 16:42
 */
@Data
@Slf4j
public class FinishRateRankVO extends DataUploadVO{

    @ApiModelProperty("完成率排行")
    public List<FinishRateVO> finishRateList;

    public static FinishRateRankVO convert(List<AchieveQyyRegionDataDO> rankList, NodeTypeEnum nodeType){
        FinishRateRankVO result = new FinishRateRankVO();
        List<FinishRateVO> finishRateRankList = new ArrayList<>();
        Date lastUpdateTime = null;
        for (AchieveQyyRegionDataDO achieveQyyRegion : rankList) {
            FinishRateVO sale = FinishRateVO.convert(achieveQyyRegion);
            if(Objects.isNull(lastUpdateTime)){
                lastUpdateTime = achieveQyyRegion.getEtlTm();
            }
            if(achieveQyyRegion.getEtlTm().after(lastUpdateTime)){
                lastUpdateTime = achieveQyyRegion.getEtlTm();
            }
            if(Objects.nonNull(nodeType) && nodeType.equals(NodeTypeEnum.STORE)){
                String storeHomeUrl = MessageFormat.format(Constants.AK_STORE_HOME, achieveQyyRegion.getThirdDeptId());
                sale.setStoreHomeUrl(storeHomeUrl);
            }
            finishRateRankList.add(sale);
        }
        List<FinishRateVO> collect = finishRateRankList.stream().sorted(Comparator.comparing(FinishRateVO::getSalesRate).reversed().thenComparing(FinishRateVO::getSalesAmtZzl)).collect(Collectors.toList());
        log.info("collect:{}",JSONObject.toJSONString(collect));
        result.setFinishRateList(collect);
        if(Objects.nonNull(lastUpdateTime)){
            result.setEtlTm(DateUtil.format(lastUpdateTime, "yyyy-MM-dd HH:mm"));
        }
        return result;
    }

}
