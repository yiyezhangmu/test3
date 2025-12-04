package com.coolcollege.intelligent.model.homeTemplate;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 13:55
 * @Version 1.0
 */
@Data
public class HomeTemplateDO {
    /**
     *主键
     */
    private Integer id;
    /**
     *首页模板名称
     */
    private String templateName;
    /**
     *首页模板描述
     */
    private String templateDescription;
    /**
     *系统默认标识
     */
    private Integer isDefault;
    /**
     *是否删除:0:未删除，1.删除
     */
    private Integer deleted;
    /**
     *PC组件json
     */
    private String pcComponentsJson;
    /**
     *APP组件json
     */
    private String appComponentsJson;
    /**
     *创建人
     */
    private String createId;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *更新人
     */
    private String updateId;
    /**
     *更新时间
     */
    private Date updateTime;
}
