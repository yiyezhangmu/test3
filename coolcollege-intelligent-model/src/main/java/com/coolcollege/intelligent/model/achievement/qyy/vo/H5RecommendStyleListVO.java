package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.model.qyy.QyyRecommendStyleDO;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: H5RecommendStyleListVO
 * @Description: 主推款列表
 * @date 2023-04-04 17:06
 */
@Data
public class H5RecommendStyleListVO {

    @ApiModelProperty("主推款id")
    private Long id;

    @ApiModelProperty("主推款名称")
    private String name;

    @ApiModelProperty("商品数量")
    private Integer goodsNum;

    public static List<H5RecommendStyleListVO> convert(List<QyyRecommendStyleDO> recommendStyleList){
        if(CollectionUtils.isEmpty(recommendStyleList)){
            return Lists.newArrayList();
        }
        List<H5RecommendStyleListVO> resultList = new ArrayList<>();
        for (QyyRecommendStyleDO recommendStyle : recommendStyleList) {
            H5RecommendStyleListVO result = new H5RecommendStyleListVO();
            result.setId(recommendStyle.getId());
            result.setName(recommendStyle.getName());
            result.setGoodsNum(recommendStyle.getGoodsNum());
            resultList.add(result);
        }
        return resultList;
    }


}
