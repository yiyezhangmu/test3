package com.coolcollege.intelligent.model.sop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author 邵凌志
 * @date 2021/2/20 16:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class TaskSopClassifyDO {

    private Long id;

    /**
     * 分类名称
     */
    private String classifyName;
    /**
     * 上传人id
     */
    private String createUserId;
    /**
     * 上传人
     */
    private String createUser;
    /**
     * 上传时间
     */
    private Date createTime;
    /**
     * 更新人id
     */
    private String updateUserId;
    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 更新时间
     */
    private Date updateTime;
}
