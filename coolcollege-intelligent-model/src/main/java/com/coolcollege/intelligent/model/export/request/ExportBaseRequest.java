package com.coolcollege.intelligent.model.export.request;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author byd
 * @date 2021-05-20 20:20
 */
@Data
public class ExportBaseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库
     */
    private String dbName;

    private Integer pageSize;

    private Integer pageNum;

    /**
     * 导出任务
     */
    private ImportTaskDO importTaskDO;
    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 导出总数
     */
    private Long totalNum;
}
