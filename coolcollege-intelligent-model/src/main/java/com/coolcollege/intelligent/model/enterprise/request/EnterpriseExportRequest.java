package com.coolcollege.intelligent.model.enterprise.request;

import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import lombok.Data;

/**
 * @author: xuanfeng
 * @date: 2021-09-17 14:12
 */
@Data
public class EnterpriseExportRequest{

    private String name;

    private String eid;

    private Boolean isPersonal;
}
