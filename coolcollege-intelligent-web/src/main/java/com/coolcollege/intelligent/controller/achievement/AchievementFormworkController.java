package com.coolcollege.intelligent.controller.achievement;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.achievement.dto.AchievementFormworkDTO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementFormworkMappingDTO;
import com.coolcollege.intelligent.model.achievement.request.AchievementRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementFormworkVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.AchievementFormworkService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 业绩模板
 *
 * @author chenyupeng
 * @since 2021/10/25
 */
@RestController
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/achievement/achievementFormwork")
public class AchievementFormworkController {
    @Resource
    private AchievementFormworkService achievementFormworkService;

    @PostMapping("save")
    public ResponseResult save(@PathVariable("enterprise-id") String enterpriseId,
                               @RequestBody AchievementFormworkDTO dto) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        achievementFormworkService.saveFormwork(enterpriseId, dto, user);
        return ResponseResult.success();
    }

    @PostMapping("update")
    public ResponseResult update(@PathVariable("enterprise-id") String enterpriseId,
                                 @RequestBody AchievementFormworkDTO dto) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        achievementFormworkService.updateFormwork(enterpriseId, dto, user);
        return ResponseResult.success();
    }

    @PostMapping("updateMappingStatus")
    public ResponseResult update(@PathVariable("enterprise-id") String enterpriseId,
                                 @RequestBody AchievementFormworkMappingDTO dto) {
        DataSourceHelper.changeToMy();
        achievementFormworkService.updateMappingStatus(enterpriseId, dto);
        return ResponseResult.success();
    }

    @GetMapping("listAll")
    public ResponseResult<List<AchievementFormworkVO>> listAll(@PathVariable("enterprise-id") String enterpriseId,
                                                               @RequestParam(value = "statusStr")String statusStr) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementFormworkService.listAllFormwork(enterpriseId,statusStr));
    }

    @GetMapping("list")
    public ResponseResult<Map<String,Object>> list(@PathVariable("enterprise-id") String enterpriseId,
                                                    AchievementRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(PageHelperUtil.getPageInfo(achievementFormworkService.listFormwork(enterpriseId,request)));
    }

    @GetMapping("get")
    public ResponseResult<AchievementFormworkVO> get(@PathVariable("enterprise-id") String enterpriseId,
                                                      @RequestParam(value = "id")Long id,
                                                      @RequestParam(value = "statusStr",required = false)String statusStr) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(achievementFormworkService.getFormwork(enterpriseId,id,statusStr));
    }
}
