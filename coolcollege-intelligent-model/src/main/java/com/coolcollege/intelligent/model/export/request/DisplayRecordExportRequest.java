package com.coolcollege.intelligent.model.export.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author shuchang.wei
 * @date 2021/6/21 16:09
 */
@Data
public class DisplayRecordExportRequest extends DynamicFieldsExportRequest{
    @NotNull(message = "unifyTaskId不能为空")
    private Long unifyTaskId;
}
