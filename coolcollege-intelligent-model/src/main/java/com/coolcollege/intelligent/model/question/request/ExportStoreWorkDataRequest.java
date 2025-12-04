package com.coolcollege.intelligent.model.question.request;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

/**
 * 导出店务门店统计
 * @author wxp
 * @date 2022-9-26 16:16
 */
@Data
public class ExportStoreWorkDataRequest extends ExportBaseRequest{

    private StoreWorkDataListRequest request;

    private CurrentUser user;

}
