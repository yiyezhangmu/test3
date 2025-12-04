package com.coolcollege.intelligent.controller.achievement;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.achievement.request.AchievementRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTypeReqVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTypeResVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.AchievementTypeService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Description: 业绩类型controller
 * @Author: mao
 * @CreateDate: 2021/5/20
 */
@Slf4j
@RestController
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/achievement/achievementType")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AchievementTypeController {

    private final AchievementTypeService achievementTypeService;

    @PostMapping("/add")
    public ResponseResult<AchievementTypeResVO> createType(@PathVariable("enterprise-id") String enterpriseId,
                                                           @RequestBody  AchievementTypeReqVO req) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(achievementTypeService.insertAchievementType(enterpriseId, req,user));
    }

    @GetMapping("/query")
    public ResponseResult<List<AchievementTypeResVO>> queryAllType(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementTypeService.listAchievementTypes(enterpriseId));
    }

    @PostMapping("/update")
    public ResponseResult<AchievementTypeResVO> updateType(@PathVariable("enterprise-id") String enterpriseId,
                                                           @RequestBody  AchievementTypeReqVO req) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(achievementTypeService.updateType(enterpriseId, req,user));
    }

    @PostMapping("/delete")
    public ResponseResult<AchievementTypeResVO> deleteType(@PathVariable("enterprise-id") String enterpriseId,
                                                           @RequestBody @Valid AchievementTypeReqVO req) {
        DataSourceHelper.changeToMy();
        achievementTypeService.deleteType(enterpriseId, req);
        return ResponseResult.success();
    }

    @GetMapping("/queryLastEdit")
    public ResponseResult<AchievementTypeResVO> queryLastEdit(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementTypeService.getLatEdit(enterpriseId));
    }

    @GetMapping("/list")
    public ResponseResult<Map<String, Object>> list(@PathVariable("enterprise-id") String enterpriseId,
                                                    AchievementRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(PageHelperUtil.getPageInfo(achievementTypeService.list(enterpriseId,request)));
    }

}
