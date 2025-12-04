package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.model.qyy.QyyRecommendStyleDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: H5RecommendStyleDetailVO
 * @Description: 主推款移动端详情
 * @date 2023-04-04 17:09
 */
@Data
public class H5RecommendStyleDetailVO {

    @ApiModelProperty("主推款id")
    private Long id;

    @ApiModelProperty("主推款名称")
    private String name;

    @ApiModelProperty("课程信息")
    private String courseInfo;

    @ApiModelProperty("主推款列表")
    private List<RecommendStyleGoodsVO> goodsList;

    public static H5RecommendStyleDetailVO convert(QyyRecommendStyleDO param){
        if(Objects.isNull(param)){
            return null;
        }
        H5RecommendStyleDetailVO result = new H5RecommendStyleDetailVO();
        result.setId(param.getId());
        result.setName(param.getName());
        result.setCourseInfo(param.getCourseInfo());
        return result;
    }

}
