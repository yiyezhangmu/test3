package com.coolcollege.intelligent.controller.dingChatSync;

import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.facade.SyncDeptFacade;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author suzhuhong
 * @Date 2022/8/4 14:41
 * @Version 1.0
 */
@RestController
@RequestMapping({"/v3/sync/"})
@Slf4j
public class DingChatSyncController {

    @Autowired
    private SyncDeptFacade syncDeptFacade;
    /**
     * 钉钉同步 + 企微同步，通过eid区别
     * @param eid
     * @return
     */
    @PostMapping("/{enterprise-id}/newSyncAll")
    @OperateLog(operateModule = CommonConstant.Function.ENTERPRISE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "钉钉同步")
    public ResponseResult dingSync(@PathVariable("enterprise-id") String eid,
                                   @RequestParam(value = "regionId", required = false) Long regionId) {
        DataSourceHelper.changeToMy();
        syncDeptFacade.newSync(eid, UserHolder.getUser().getName(), String.valueOf(UserHolder.getUser().getId()),regionId);
        return ResponseResult.success();
    }
}
