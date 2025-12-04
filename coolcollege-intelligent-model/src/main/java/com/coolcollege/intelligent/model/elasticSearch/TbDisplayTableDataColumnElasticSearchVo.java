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
public class TbDisplayTableDataColumnElasticSearchVo {
    private Long id ;

    private String storeId;

    private Date createTime;

    private Long regionId;

    private String remark;

    private Long recordId;

    private Integer score;

    private Date updateTime;

    private Integer deleted;

    private Long loopCount;

    private  Long unifyTaskId;

    private Long metaColumnId;

    private Long dataTableId;

    private String storeName;

    private String photoArray;

    private String regionPath;

    private Long metaTableId;

    private String taskName;


}
