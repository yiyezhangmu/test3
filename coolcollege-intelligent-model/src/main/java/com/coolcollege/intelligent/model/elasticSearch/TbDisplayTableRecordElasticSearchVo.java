package com.coolcollege.intelligent.model.elasticSearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2021/8/16 9:54
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTableRecordElasticSearchVo {

    private Long id ;

    private String storeId;

    private Date createTime;

    private String attachUrl;

    private String isSupportPhoto;

    private Long regionId;

    private String handleUserId;

    private String remark;

    private String handleUserName;

    private Integer score;

    private Date updateTime;

    private Integer deleted;

    private Long loopCount;

    private Long unifyTaskId;

    private String storeName;

    private String regionPath;

    private Long metaTableId;

    private String isSupportScore;

    private String status;

    private String taskName;

}
