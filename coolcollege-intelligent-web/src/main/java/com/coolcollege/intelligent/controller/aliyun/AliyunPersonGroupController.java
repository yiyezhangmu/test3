package com.coolcollege.intelligent.controller.aliyun;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonGroupAddRequest;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonGroupBaseRequest;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonGroupUpdateRequest;
import com.coolcollege.intelligent.service.aliyun.AliyunPersonGroupService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/26
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v3/enterprises/{enterprise-id}/aliyun/person/group")
public class AliyunPersonGroupController {

    @Autowired
    private AliyunPersonGroupService aliyunPersonGroupService;

    @GetMapping("/list")
    public ResponseResult listAliyunPersonGroup(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aliyunPersonGroupService.listAliyunPersonGroup(enterpriseId));
    }

    @PostMapping("/add")
    @OperateLog(operateModule = CommonConstant.Function.STATIC_PERSON_GROUP, operateType = CommonConstant.LOG_ADD, operateDesc = "新增")
    public ResponseResult addAliyunPersonGroup(@PathVariable("enterprise-id") String enterpriseId,
                                               @RequestBody AliyunPersonGroupAddRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aliyunPersonGroupService.addAliyunPersonGroup(enterpriseId,request));

    }
    @PostMapping("/update")
    @OperateLog(operateModule = CommonConstant.Function.STATIC_PERSON_GROUP, operateType = CommonConstant.LOG_UPDATE, operateDesc = "修改")
    public ResponseResult updateAliyunPersonGroup(@PathVariable("enterprise-id") String enterpriseId,
                                               @RequestBody AliyunPersonGroupUpdateRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aliyunPersonGroupService.updateAliyunPersonGroup(enterpriseId,request));
    }
    @PostMapping("/delete")
    @OperateLog(operateModule = CommonConstant.Function.STATIC_PERSON_GROUP, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除")
    public ResponseResult deleteAliyunPersonGroup(@PathVariable("enterprise-id") String enterpriseId,
                                                  @RequestBody AliyunPersonGroupBaseRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aliyunPersonGroupService.deleteAliyunPersonGroup(enterpriseId,request.getGroupId()));
    }

}
