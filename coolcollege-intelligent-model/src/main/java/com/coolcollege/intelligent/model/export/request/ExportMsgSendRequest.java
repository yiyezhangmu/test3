package com.coolcollege.intelligent.model.export.request;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import lombok.Data;

/**
 * @author shuchang.wei
 * @date 2021/6/4 11:51
 */
@Data
public class ExportMsgSendRequest {
    /**
     * 导出任务
     */
    private ImportTaskDO importTaskDO;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 导出总数
     */
    private Long totalNum;

    /**
     * 请求参数
     */
    private JSONObject request;

    /**
     * 是否添加10级区域字段
     */
    private Boolean isAddRegion;

}
