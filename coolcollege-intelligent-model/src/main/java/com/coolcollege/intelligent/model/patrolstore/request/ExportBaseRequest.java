package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author byd
 * @date 2021-05-21 9:52
 */
@Data
public class ExportBaseRequest extends FileExportBaseRequest implements Serializable {


    private static final long serialVersionUID = 1L;

    String enterpriseId;

    ImportTaskDO importTaskDO;

    /**
     * 总数量
     */
    Long totalNum;

    private String dbName;
}
