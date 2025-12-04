package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbMetaStaColumnVO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * ID
     */
    private Long businessId;

    /**
     * ID
     */
    private Long metaColumnId;

    /**
     * ID
     */
    private Long metaTableId;



    /**
     * 自定义名称
     */
    private String columnName;

    /**
     * 默认分值
     */
    private BigDecimal checkScore;

    /**
     * 奖金翻倍
     */
    private BigDecimal awardTimes;
    /**
     * 得分翻倍
     */
    private BigDecimal scoreTimes;

    /**
     *  标准分值
     */
    private BigDecimal supportScore;

    /**
     * 结果
     */
    private String checkResult;


    /**
     * 结果
     */
    @Excel(name = "检查结果")
    private String checkResultName;

    private String checkPics;


    /**
     * 备注
     */
    private String checkText;

    private Long checkResultId;

    private Long taskId;

    private Long regionId;

    private Date createTime;

    /**
     * 门店场景名称
     */
    private String storeSceneName;

    /**
     * 门店场景id
     */
    private Long storeSceneId;

    private String supervisorId;

    private String statisticalDimension;

    private Integer columnType;

    private BigDecimal weightPercent;

    private BigDecimal columnMaxScore;

    private BigDecimal columnMaxAward;

    private BigDecimal rewardPenaltMoney;

    private String checkResultReason;

    private String patrolType;

    /**
     * 数据表id
     */
    private Long dataTableId;

    public String getStatisticalDimension(){
        if(StringUtils.isNotBlank(this.checkResult)){
            if("FAIL".equals(this.checkResult)){
                return "不合格";
            }else if("INAPPLICABLE".equals(this.checkResult)){
                return "不适用";
            }else if("PASS".equals(this.checkResult)){
                return "合格";
            }
        }
        return null;
    }


    public String getCheckResultName(){
        if(StringUtils.isNotBlank(this.checkResultName)){
            return this.checkResultName;
        }
        if(StringUtils.isNotBlank(this.checkResult)){
            if("FAIL".equals(this.checkResult)){
                return "不合格";
            }else if("INAPPLICABLE".equals(this.checkResult)){
                return "不适用";
            }else if("PASS".equals(this.checkResult)){
                return "合格";
            }
        }
        return null;
    }

    private static final long serialVersionUID = 1L;

    private String checkVideo;

    public String getCheckInfo(){
        String summaryInfo = this.checkPics;
        if(StringUtils.isBlank(this.checkVideo)){
            return summaryInfo;
        }
        SmallVideoInfoDTO smallVideoInfoDTO = JSONObject.parseObject(this.checkVideo, SmallVideoInfoDTO.class);
        if(CollectionUtils.isEmpty(smallVideoInfoDTO.getVideoList()) && CollectionUtils.isEmpty(smallVideoInfoDTO.getSoundRecordingList())){
            return summaryInfo;
        }
        if(CollectionUtils.isNotEmpty(smallVideoInfoDTO.getVideoList())){
            StringBuilder videoSb = new StringBuilder();
            for (SmallVideoDTO smallVideoDTO : smallVideoInfoDTO.getVideoList()) {
                if(StringUtils.isNotBlank(smallVideoDTO.getVideoUrl())){
                    videoSb.append(",").append(smallVideoDTO.getVideoUrl());
                }
            }
            summaryInfo += videoSb.toString();
        }

        if(CollectionUtils.isNotEmpty(smallVideoInfoDTO.getSoundRecordingList())){
            StringBuilder soundRecordingSb = new StringBuilder();
            for (String soundRecording : smallVideoInfoDTO.getSoundRecordingList()) {
                if(StringUtils.isNotBlank(soundRecording)){
                    soundRecordingSb.append(",").append(soundRecording);
                }
            }
            summaryInfo += soundRecordingSb.toString();
        }
        return summaryInfo;
    }
}