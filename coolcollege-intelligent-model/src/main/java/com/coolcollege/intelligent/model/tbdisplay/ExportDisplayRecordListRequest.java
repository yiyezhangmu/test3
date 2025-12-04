package com.coolcollege.intelligent.model.tbdisplay;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayReportQueryParam;
import lombok.Data;

import java.io.Serializable;

/**
 * @author byd
 */
@Data
public class ExportDisplayRecordListRequest extends FileExportBaseRequest implements Serializable {


    private static final long serialVersionUID = 1L;

    TbDisplayReportQueryParam request;


    String enterpriseId;

    ImportTaskDO importTaskDO;

    /**
     * 总数量
     */
    Long totalNum;

    private String dbName;

}
