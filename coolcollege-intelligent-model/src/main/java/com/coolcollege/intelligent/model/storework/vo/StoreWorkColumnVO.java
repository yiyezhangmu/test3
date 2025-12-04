package com.coolcollege.intelligent.model.storework.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/10/12 10:42
 * @Version 1.0
 */
@Data
public class StoreWorkColumnVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("检查项名称")
    @Excel(name = "检查项名称",orderNum = "1")
    private String metaColumnName;

    @ApiModelProperty("检查项分类名称")
    @Excel(name = "检查项分类",orderNum = "2")
    private String metaColumnCategoryName;
    @ApiModelProperty("门店ID")
    private String storeId;
    @ApiModelProperty("门店名称")
    @Excel(name = "门店名称",orderNum = "3")
    private String storeName;
    @ApiModelProperty("门店编号")
    @Excel(name = "门店编号",orderNum = "4")
    private String storeNum;
    @ApiModelProperty("所属区域")
    @Excel(name = "所属区域",orderNum = "5")
    private String allRegionName;
    @ApiModelProperty("得分")
    @Excel(name = "得分",orderNum = "6")
    private BigDecimal checkScore;
    @ApiModelProperty("备注")
    @Excel(name = "备注",orderNum = "7")
    private String checkText;
    @Excel(name = "现场结果", orderNum = "8")
    private String checkInfo;
    @ApiModelProperty("图片")
    private String checkPics;
    @ApiModelProperty("视频录音")
    private String checkVideo;
    @ApiModelProperty("执行人id")
    private String handlerUserId;
    @ApiModelProperty("执行人姓名")
    @Excel(name = "执行人姓名",orderNum = "9")
    private String handlerUserName;
    @ApiModelProperty("执行时间")
    @Excel(name = "执行时间",format = "yyyy.MM.dd HH:mm",orderNum = "10")
    private Date submitTime;
    @ApiModelProperty("点评结果")
    @Excel(name = "点评结果",replace = {"不合格_FAIL", "不适用_INAPPLICABLE","合格_PASS", "_null"},orderNum = "11")
    private String checkResult;
    @ApiModelProperty("点评时间")
    @Excel(name = "点评时间",format = "yyyy.MM.dd HH:mm",orderNum = "12")
    private Date commentTime;
    private String actualCommentUserId;
    @Excel(name = "点评人",orderNum = "12")
    private String actualCommentUserName;
    @Excel(name = "点评描述",orderNum = "12")
    private String commentContent;
    @ApiModelProperty("工作表名称")
    @Excel(name = "工作表名称",orderNum = "13")
    private String tableName;
    @ApiModelProperty("店务名称")
    @Excel(name = "店务名称",orderNum = "14")
    private String storeWorkName;
    @ApiModelProperty("店务类型")
    @Excel(name = "店务类型",replace = {"日清_DAY", "周清_WEEK","月清_MONTH", "_null"},orderNum = "15")
    private String workCycle;
    @ApiModelProperty("检查项分值")
    @Excel(name = "检查项分值",orderNum = "16")
    private BigDecimal score;
    @ApiModelProperty("标准图")
    @Excel(name = "标准图",orderNum = "17")
    private String staPic;

    /**
     * 格式：单选Radio，多选Checkbox，当行文本Input，多行文本Textarea，数字，日期，图片
     */
    private String format;

    @ApiModelProperty("值1")
    private String value1;

    @ApiModelProperty("值2")
    private String value2;

    @ApiModelProperty("表属性")
    private Integer tableProperty;

    @ApiModelProperty("AI检查项结果:PASS,FAIL,INAPPLICABLE")
    private String aiCheckResult;

    @ApiModelProperty("AI检查项结果id")
    private Long aiCheckResultId;

    @ApiModelProperty("AI检查项结果名称")
    private String aiCheckResultName;

    @ApiModelProperty("AI点评内容")
    private String aiCommentContent;

    @ApiModelProperty("AI检查项分值")
    private BigDecimal aiCheckScore;

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
