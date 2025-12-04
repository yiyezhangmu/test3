package com.coolcollege.intelligent.model.metatable.request;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

/**
 * @author chenyupeng
 * @since 2022/4/13
 */
@Data
public class TbMetaQuickColumnExportRequest  extends FileExportBaseRequest {

    /**
     * 检查项名称
     */
    private String columnName;

    /**
     * 检查项类型
     */
    private Integer columnType;

    /**
     * 检查表属性
     */
    private Integer tableProperty;

    private Long categoryId;

    private boolean create;

    private CurrentUser user;

    private Integer status;
}
