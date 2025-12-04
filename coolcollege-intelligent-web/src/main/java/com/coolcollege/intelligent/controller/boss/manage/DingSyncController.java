package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.facade.NewSyncFacade;
import com.coolcollege.intelligent.facade.SyncDeptFacade;
import com.coolcollege.intelligent.model.userholder.BossUserHolder;
import com.coolcollege.intelligent.util.DingTalkLimitingUtil;
import com.taobao.api.ApiException;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author byd
 */
@RestController
@BaseResponse
@RequestMapping({"/boss/manage/dingSync"})
public class DingSyncController {


    @Autowired
    private SyncDeptFacade syncDeptFacade;

    /**
     * 钉钉同步 + 企微同步，通过eid区别
     * @param eid
     * @return
     */
    @PostMapping("/{enterprise-id}/syncAll")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "钉钉同步")
    public void dingSync(@PathVariable("enterprise-id") String eid) throws ApiException {
        DataSourceHelper.changeToMy();
        syncDeptFacade.sync(eid, BossUserHolder.getUser().getName(), String.valueOf(BossUserHolder.getUser().getId()));
    }

    /**
     * 钉钉同步 + 企微同步，通过eid区别
     * @param eid
     * @return
     */
    @PostMapping("/{enterprise-id}/newSyncAll")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "钉钉同步")
    public void dingSync(@PathVariable("enterprise-id") String eid,
                         @RequestParam Long regionId) {
        DataSourceHelper.changeToMy();
        syncDeptFacade.newSync(eid, BossUserHolder.getUser().getName(), String.valueOf(BossUserHolder.getUser().getId()),regionId);
    }

    @GetMapping("/{enterprise-id}/clearSyncLimit")
    public void clearSyncLimit(@PathVariable("enterprise-id") String eid) {
        DataSourceHelper.changeToMy();
        syncDeptFacade.clearSyncLimit(eid);
    }
}
