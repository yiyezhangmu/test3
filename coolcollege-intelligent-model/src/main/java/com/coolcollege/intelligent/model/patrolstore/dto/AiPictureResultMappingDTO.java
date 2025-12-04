package com.coolcollege.intelligent.model.patrolstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author chenyupeng
 * @since 2022/4/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPictureResultMappingDTO {

    /**
     * id
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 巡检图片id
     */
    private Long pictureId;
    /**
     * 检查项id
     */
    private Long metaColumnId;
    /**
     * ai分析结果 合格:pass、不合格:failed、不适用:unapplicable
     */
    private String aiResult;
    /**
     * ai分析内容
     */
    private List<String> aiContent;
    /**
     * 图片url
     */
    private String picUrl;
    /**
     * 显示先后顺序
     */
    private int sortNum;
}
