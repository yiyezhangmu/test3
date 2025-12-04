package com.coolcollege.intelligent.controller.achievement;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.model.achievement.request.AchievementTargetExportRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementTargetRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetExportVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetStoreVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetVO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseBossExportDTO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.AchievementTargetService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * @Description: 业绩门店目标controller
 * @Author: mao
 * @CreateDate: 2021/5/20
 */
@Slf4j
@RestController
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/achievement/achievementTarget")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AchievementTargetController {

    private final AchievementTargetService achievementTargetService;

    @PostMapping("/add")
    public ResponseResult<AchievementTargetVO> createTarget(@PathVariable("enterprise-id") String enterpriseId,
                                                             @RequestBody AchievementTargetVO req) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(achievementTargetService.saveAchievementTarget(enterpriseId,req,user));
    }

    @PostMapping("/query")
    public ResponseResult queryPageTarget(@PathVariable("enterprise-id") String enterpriseId,@RequestBody @Valid AchievementTargetVO req) {
        DataSourceHelper.changeToMy();
        return ResponseResult
            .success(achievementTargetService.listTargetPages(enterpriseId,req));
    }

    @PostMapping("/update")
    public ResponseResult updateTarget(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementTargetVO req) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        return ResponseResult.success(achievementTargetService.updateAchievementTarget(enterpriseId, req,user));
    }

    @PostMapping("/delete")
    public ResponseResult deleteTarget(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementTargetVO req) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        achievementTargetService.deleteTarget(enterpriseId, req,user);
        return ResponseResult.success();
    }

    @PostMapping("/listByStoreAndTime")
    public ResponseResult<List<AchievementTargetStoreVO>> listByStoreAndTime(@PathVariable("enterprise-id") String enterpriseId,@RequestBody  AchievementTargetRequest req) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementTargetService.listByStoreAndTime(enterpriseId,req));
    }

    @PostMapping("/updateTargetDetailBatch")
    public ResponseResult updateTargetDetailBatch(@PathVariable("enterprise-id") String enterpriseId, @RequestBody AchievementTargetRequest req) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        achievementTargetService.updateTargetDetailBatch(enterpriseId,req,user);
        return ResponseResult.success();
    }

    @GetMapping("/getByStoreIdAndYear")
    public ResponseResult getById(@PathVariable("enterprise-id") String enterpriseId,
                                  String storeId,
                                  Integer achievementYear) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementTargetService.getByStoreIdAndYear(enterpriseId,storeId,achievementYear));
    }

    @GetMapping("/downloadTemplate")
    public ResponseResult<ImportTaskDO> downloadTemplate(@PathVariable("enterprise-id") String enterpriseId,
                                                         AchievementTargetExportRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setUser(user);
        return ResponseResult.success(achievementTargetService.downloadTemplate(enterpriseId,request,user));
    }

    @GetMapping("/exportTemplate")
    public ResponseResult<ImportTaskDO> exportTemplate(@PathVariable("enterprise-id") String enterpriseId,
                                                         AchievementTargetExportRequest request) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        request.setUser(user);
        return ResponseResult.success(achievementTargetService.exportTemplate(enterpriseId,request,user));
    }

}
