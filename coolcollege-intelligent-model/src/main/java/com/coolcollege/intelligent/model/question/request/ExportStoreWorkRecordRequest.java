package com.coolcollege.intelligent.model.question.request;

import com.coolcollege.intelligent.model.patrolstore.request.ExportBaseRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import lombok.Data;

/**
 * 导出店务门店统计
 * @author wxp
 * @date 2022-10-09 16:16
 */
@Data
public class ExportStoreWorkRecordRequest extends ExportBaseRequest{

    private StoreWorkRecordListRequest request;

    private CurrentUser user;
}
