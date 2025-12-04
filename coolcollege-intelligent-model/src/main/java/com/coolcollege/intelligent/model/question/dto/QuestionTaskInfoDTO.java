package com.coolcollege.intelligent.model.question.dto;

import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.inspection.AiInspectionStoreFailPictureDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 工单任务信息定义表信息
 * @author byd
 * @date 2022-08-12 10:48
 */
@ApiModel
@Data
public class QuestionTaskInfoDTO {

    /**
     * 图片
     */
    @ApiModelProperty("图片")
    private List<String> photos;

    /**
     * 视频
     */
    @ApiModelProperty("视频")
    private String videos;

    /**
     * 巡店不合格数据项id
     */
    @ApiModelProperty("数据项id")
    private Long dataColumnId;

    /**
     * 巡店记录id or 店务记录id与businessId可以互换使用
     */
    @ApiModelProperty("巡店记录id")
    private Long businessId;

    /**
     * 检查项id
     */
    @ApiModelProperty("检查项id")
    private Long metaColumnId;

    /**
     * 检查项名称
     */
    @ApiModelProperty("检查项名称")
    private String metaColumnName;

    /**
     * 培训内容高级设置：先学习后处理工单（1）、边学边处理（0）
     */
    @ApiModelProperty("培训内容高级设置：先学习后处理工单（1）、边学边处理（0）")
    private Boolean contentLearnFirst;

    /**
     * 录音列表
     */
    @ApiModelProperty("录音列表")
    private List<String> soundRecordingList;

    /**
     *    1:手动 2：自动        QuestionCreateTypeEnum.AUTOMATIC.getCode()
     */
    @ApiModelProperty("工单发起方式:1:手动 2：自动")
    private Integer createType;

    /**
     * 课程列表
     */
    @ApiModelProperty("课程列表")
    private List<CoolCourseVO> courseList;

    /**
     * 附件
     */
    @ApiModelProperty("附件")
    private String attachUrl;

    @ApiModelProperty("开蚝屋检查项宜搭分数")
    private String dedPoints;

    @ApiModelProperty("开蚝屋问题类型（就是检查项名称）")
    private String questionTypeName;

    @ApiModelProperty("不合格图片以及描述列表")
    List<AiInspectionStoreFailPictureDTO> failPictureList;
}
