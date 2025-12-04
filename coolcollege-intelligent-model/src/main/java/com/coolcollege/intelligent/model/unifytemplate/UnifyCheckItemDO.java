package com.coolcollege.intelligent.model.unifytemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description for this class
 *
 * @author : lz
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/18 16:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifyCheckItemDO {
    private Long id;
    private String name;

    /**
     * 图片链接
     */
    private String picUrl;
    private String description;
    /**
     * 批量查询时带出模板ID，方便抽取sql
     */
    private Long displayTemplateId;
    private Long isInitialized;
    private Long deleteIs;
    private Long createTime;
    private String createUserId;
    private Long updateTime;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;
}
