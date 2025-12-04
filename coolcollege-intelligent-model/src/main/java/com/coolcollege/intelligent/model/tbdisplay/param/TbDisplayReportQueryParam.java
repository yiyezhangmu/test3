package com.coolcollege.intelligent.model.tbdisplay.param;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 陈列记录报表查询参数
 * 
 * @author wxp
 * @date 2021-3-8 20:07
 */
@Data
public class TbDisplayReportQueryParam extends FileExportBaseRequest implements Serializable {

    private static final long serialVersionUID = -7897701301446156205L;

    /**
     * 页码
     */
    private Integer pageNumber = 1;
    /**
     *
     */
    private Integer pageSize = 20;
    /**
     *
     */
    @NotNull(message = "unifyTaskId不能为空")
    private Long unifyTaskId;

    /**
     * 循环轮次
     */
    private Long loopCount;

    /**
     * 企业id
     */
    private String eid;

    /**
     * 企业库
     */
    private String dbName;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 是否导出图片
     */
    private Boolean picture = false;

    /**
     * 抄送人id
     */
    private String ccUserId;

    /**
     * 处理人id
     */
    private String userId;

}
