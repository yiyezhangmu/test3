package com.coolcollege.intelligent.model.patrolstore.request;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/07/06
 */
@Data
public class ExportStoreBaseRequest extends ExportBaseRequest {

    private Boolean isAdmin;
    private List<String> storeIdList;
    private List<String> fullRegionIdList;


}
