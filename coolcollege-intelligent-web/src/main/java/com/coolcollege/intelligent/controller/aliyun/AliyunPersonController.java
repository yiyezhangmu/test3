package com.coolcollege.intelligent.controller.aliyun;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonBaseRequest;
import com.coolcollege.intelligent.model.aliyun.request.AliyunPersonUpdateRequest;
import com.coolcollege.intelligent.model.aliyun.request.AliyunStaticPersonAddRequest;
import com.coolcollege.intelligent.model.aliyun.request.DynamicPersonBindRequest;
import com.coolcollege.intelligent.service.aliyun.AliyunPersonService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/24
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v3/enterprises/{enterprise-id}/aliyun/person")
public class AliyunPersonController {
    @Resource
    private AliyunPersonService aliyunPersonService;

    @PostMapping("/add")
    @OperateLog(operateModule = CommonConstant.Function.STATIC_PERSON, operateType = CommonConstant.LOG_ADD, operateDesc = "新增")
    public Object addStaticAliyunPerson(@PathVariable("enterprise-id") String enterpriseId,
                                        @RequestBody @Valid AliyunStaticPersonAddRequest request) {
        DataSourceHelper.changeToMy();
        return aliyunPersonService.addStaticAliyunPerson(enterpriseId, request);
    }

    @PostMapping("/update")
    @OperateLog(operateModule = CommonConstant.Function.STATIC_PERSON, operateType = CommonConstant.LOG_UPDATE, operateDesc = "修改")
    public ResponseResult updateAliyunPerson(@PathVariable("enterprise-id") String enterpriseId,
                                             @RequestBody @Valid AliyunPersonUpdateRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aliyunPersonService.updateAliyunPerson(enterpriseId, request));
    }


    @GetMapping("/detail")
    public Object addAliyunPerson(@PathVariable("enterprise-id") String enterpriseId,
                                  @RequestParam(value = "customerId",required = false) String customerId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aliyunPersonService.getAliyunPerson(enterpriseId, customerId));
    }

    @GetMapping("/list")
    public Object listAliyunPerson(@PathVariable("enterprise-id") String enterpriseId,
                                   @RequestParam(value = "groupId") String groupId,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNumber,
                                   @RequestParam(value = "keywords", required = false) String keywords) {
        DataSourceHelper.changeToMy();
        return aliyunPersonService.listAliyunPerson(enterpriseId, groupId, pageSize, pageNumber, keywords);
    }





    /**
     * 返回没有绑定的VIP列表数据 /写死了groupName（废弃）
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("/dynamic/bind/list")
    public Object bindAliyunDynamicPersonList(@PathVariable("enterprise-id") String enterpriseId,
                                              @RequestParam(value = "page_size", defaultValue = "10") Integer pageSize,
                                              @RequestParam(value = "page_number", defaultValue = "1") Integer pageNumber,
                                              @RequestParam(value = "keywords", required = false) String keywords) {
        DataSourceHelper.changeToMy();
        return aliyunPersonService.bindAliyunDynamicPersonList(enterpriseId, pageSize, pageNumber, keywords);

    }


    @PostMapping("/delete")
    @OperateLog(operateModule = CommonConstant.Function.STATIC_PERSON, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除")
    public ResponseResult deleteAliyunPerson(@PathVariable("enterprise-id") String enterpriseId,
                                             @RequestBody @Valid AliyunPersonBaseRequest request) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(aliyunPersonService.deleteAliyunPerson(enterpriseId, request.getCustomerId()));

    }


}
