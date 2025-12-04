package com.coolcollege.intelligent.model.activity;

import lombok.Data;

import java.util.Date;

@Data
public class BroadcastCommentWarningLogDO {

    private Long id;

    /**
     * 评论ID/回复ID
     */

    private Long targetId;

    /**
     * 敏感词类型1-评论2-回复
     */
    private Integer type;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 检查结果
     */
    private String reason;

    /**
     * 风险等级: high-高风险 medium-中风险 low-低风险
     */

    private String riskLevel;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 删除标识
     */
    private Boolean deleted;

}
