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
public class UnifyCheckItemSnapshotDO {
    /**
     * type=DISPLAY
     *      *  记录检查表ID    display_template.Id
     *
     * type=DISPLAY_PG
     *      *  记录检查表快照 ID  display_template_snapshot.snapshotId
     *
     */
    private Long displayTemplateId;

    private Long snapshotId;
    private String snapshotCreateTime;
    private String snapshotCreateUserId;

    private Long id;
    private String name;
    /**
     * 图片链接
     */
    private String picUrl;
    private String description;
    private Integer isInitialized;
    private Integer deleteIs;
    private Long createTime;
    private String createUserId;
    private Long updateTime;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;
}
