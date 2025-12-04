package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.model.enterprise.vo.EnterprisePatrolBiosVO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 邵凌志
 * @date 2021/1/28 9:38
 */
@RestController
@RequestMapping({"/boss/manage/bossPatrol"})
@BaseResponse
@Slf4j
public class BossPatrolController {

    @Autowired
    EnterpriseStoreCheckSettingService enterpriseStoreCheckSettingService;


    /**
     * 新增巡店bios配置
     * @param level
     * @return
     */
    @PostMapping("/updateCheckResultAndLevel")
    public Boolean updateCheckResultAndLevel(@RequestParam("enterpriseId") String eid,
                                  @RequestBody EnterprisePatrolBiosVO level) {
        DataSourceHelper.reset();
        return enterpriseStoreCheckSettingService.updateStoreBios(eid, level);
    }



}
