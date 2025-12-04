package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseFollowRecordsDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseFollowRecordsRequest;
import com.coolcollege.intelligent.model.userholder.BossUserHolder;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseFollowRecordsService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author chenyupeng
 * @since 2021/11/24
 */
@RestController
@RequestMapping("/boss/manage/enterprise/followRecords")
@BaseResponse
@Slf4j
public class BossEnterpriseFollowRecordsController {

    @Resource
    EnterpriseFollowRecordsService enterpriseFollowRecordsService;

    @GetMapping(value = "/list")
    public ResponseResult<Map<String,Object>> list(EnterpriseFollowRecordsRequest request) {
        DataSourceHelper.reset();
        return ResponseResult.success(PageHelperUtil.getPageInfo(enterpriseFollowRecordsService.listEnterpriseFollowRecords(request)));
    }

    @PostMapping(value = "/save")
    public ResponseResult<EnterpriseFollowRecordsDTO> save(@RequestBody EnterpriseFollowRecordsDTO dto) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseFollowRecordsService.saveEnterpriseFollowRecords(dto, BossUserHolder.getUser()));
    }

    @PostMapping(value = "/update")
    public ResponseResult update(@RequestBody EnterpriseFollowRecordsDTO dto) {
        DataSourceHelper.reset();
        enterpriseFollowRecordsService.updateEnterpriseFollowRecords(dto, BossUserHolder.getUser());
        return ResponseResult.success();
    }

    @PostMapping(value = "/delete")
    public ResponseResult delete(@RequestParam(value = "id") Long id) {
        DataSourceHelper.reset();
        enterpriseFollowRecordsService.deleteEnterpriseFollowRecords(id);
        return ResponseResult.success();
    }
}
