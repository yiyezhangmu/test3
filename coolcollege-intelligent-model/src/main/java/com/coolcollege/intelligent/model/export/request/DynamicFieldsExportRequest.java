package com.coolcollege.intelligent.model.export.request;

import lombok.Data;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/6/16 11:47
 */
@Data
public class DynamicFieldsExportRequest extends FileExportBaseRequest{
    List<String> fieldList;
    private String enterpriseId;
}
