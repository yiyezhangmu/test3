package com.coolcollege.intelligent.model.unifytemplate.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Description for this class
 *
 * @author : lz
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/18 21:16
 */
@Data
public class DisplayTemplateQuery implements Serializable {

    private static final long serialVersionUID = -7833701301412356205L;

    /**
     *
     */
    @JsonProperty(value= "pageNumber")
    private Integer pageNumber = 1;
    /**
     *
     */
    @JsonProperty(value= "pageSize")
    private Integer pageSize = 20;
    /**
     *模板id 为空查询所有有权限的模板
     */
    @JsonProperty(value= "unifyTemplateId")
    private Long unifyTemplateId;
    /**
     * all  全部
     * part  部分
     */
    @JsonProperty(value= "scopeType")
    private String scopeType;
    /**
     * 父任务id
     */
    @JsonProperty(value= "unifyTemplateName")
    private String unifyTemplateName;
    /**
     * 子任务id
     */
    @JsonProperty(value= "isCheckQuery")
    private Boolean isCheckQuery;

}
