package com.coolcollege.intelligent.model.patrolstore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author chenyupeng
 * @since 2022/4/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPictureResultMappingDO {

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
     * AI检查结果:PASS 合同,FAIL 不合格,INAPPLICABLE 不适用
     */
    private String aiResult;
    /**
     * ai分析内容
     */
    private String aiContent;

}
