package com.coolcollege.intelligent.model.tbdisplay.param;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
* @Description:
* @Author:
* @CreateDate: 2021-03-02 17:24:31
*/
@Data
public class TbMetaDisplayQuickColumnQueryParam {

    /**
     * 检查项名称
     */
    private String columnName;

    /**
     * 创建者
     */
    private String createUserId;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /**
     * 检查类型  1-检查内容 0-检查项
     */
    private Integer checkType ;

    /**
     *快速检查表id集合
     */
    private List<Long> tbMetaDisplayQuickColumnIds;

    /**
     * 检查内容id
     */
    private Long checkContentId;

    /**
     * 描述
     */
    private String description;
    /**
     * 标准图
     */
    private String standardPic;

    /**
     * 检查图片,0不强制1强制
     */
    private Integer mustPic;
}