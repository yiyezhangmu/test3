package com.coolcollege.intelligent.controller.recent;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.recent.RecentUseService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author 邵凌志
 * @date 2021/1/12 16:40
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/recent")
@BaseResponse
@Slf4j
public class RecentUseController {

    @Autowired
    private RecentUseService recentUseService;

    /**
     * 最近联系人
     * @param enterpriseId
     * @return
     */
    @GetMapping("user")
    public Object recentUseUserList(@PathVariable(value = "enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return recentUseService.recentUseUserList(enterpriseId);
    }

    /**
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("store")
    public Object recentUseStoreList(@PathVariable(value = "enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return recentUseService.recentUseStoreList(enterpriseId, user);
    }
}
