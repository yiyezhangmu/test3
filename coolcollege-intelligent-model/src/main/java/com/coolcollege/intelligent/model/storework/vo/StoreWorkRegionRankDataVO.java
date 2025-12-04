package com.coolcollege.intelligent.model.storework.vo;

import cn.hutool.core.util.NumberUtil;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.homepage.vo.StoreWorkDataVO;
import com.coolcollege.intelligent.model.storework.dto.PageHomeStoreWorkStatisticsDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author byd
 * @date 2022-09-21 11:18
 */
@ApiModel
@Data
public class StoreWorkRegionRankDataVO {

    @ApiModelProperty("区域Id")
    private Long regionId;

    @ApiModelProperty("区域名称")
    private String regionName;

    @ApiModelProperty("总门店数/应完成门店数")
    private Long totalNum;

    @ApiModelProperty("未完成门店数")
    private Long unFinishNum;

    @ApiModelProperty("已完成门店数")
    private Long finishNum;

    @ApiModelProperty("店务完成率")
    private BigDecimal completeRate;

    @ApiModelProperty("工单数(总)")
    private Long questionNum;

    @ApiModelProperty("平均合格率")
    private BigDecimal averagePassRate;

    @ApiModelProperty("平均得分")
    private BigDecimal averageScore;

    @ApiModelProperty("平均得分率")
    private BigDecimal averageScoreRate;

    @ApiModelProperty("平均点评率")
    private BigDecimal averageCommentRate;

    public static StoreWorkRegionRankDataVO getDataVO(Long regionId, String regionName, StoreWorkDataVO storeWorkDataVO) {
        StoreWorkRegionRankDataVO storeWorkRegionRankDataVO = new StoreWorkRegionRankDataVO();
        storeWorkRegionRankDataVO.setCompleteRate(BigDecimal.ZERO);
        storeWorkRegionRankDataVO.setAveragePassRate(BigDecimal.ZERO);
        storeWorkRegionRankDataVO.setAverageScore(BigDecimal.ZERO);
        storeWorkRegionRankDataVO.setAverageScoreRate(BigDecimal.ZERO);
        storeWorkRegionRankDataVO.setAverageCommentRate(BigDecimal.ZERO);
        storeWorkRegionRankDataVO.setQuestionNum(0L);
        storeWorkRegionRankDataVO.setTotalNum(0L);
        storeWorkRegionRankDataVO.setFinishNum(0L);
        storeWorkRegionRankDataVO.setUnFinishNum(0L);
        storeWorkRegionRankDataVO.setRegionId(regionId);
        storeWorkRegionRankDataVO.setRegionName(regionName);
        if (storeWorkDataVO == null) {
            return storeWorkRegionRankDataVO;
        }

        storeWorkRegionRankDataVO.setCompleteRate(storeWorkDataVO.getCompleteRate());
        storeWorkRegionRankDataVO.setQuestionNum(storeWorkDataVO.getQuestionNum());
        storeWorkRegionRankDataVO.setAveragePassRate(storeWorkDataVO.getAveragePassRate());
        storeWorkRegionRankDataVO.setAverageScore(storeWorkDataVO.getAverageScore());
        storeWorkRegionRankDataVO.setAverageScoreRate(storeWorkDataVO.getAverageScoreRate());
        storeWorkRegionRankDataVO.setAverageCommentRate(storeWorkDataVO.getAverageCommentRate());
        storeWorkRegionRankDataVO.setTotalNum(storeWorkDataVO.getTotalNum());
        storeWorkRegionRankDataVO.setFinishNum(storeWorkDataVO.getFinishNum());
        storeWorkRegionRankDataVO.setUnFinishNum(storeWorkDataVO.getTotalNum() - storeWorkDataVO.getFinishNum());
        return storeWorkRegionRankDataVO;
    }
}
