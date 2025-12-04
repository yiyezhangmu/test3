package com.coolcollege.intelligent.controller.openApi;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.EncryptUtil;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.facade.UnifyTaskFcade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.page.PageRequest;
import com.coolcollege.intelligent.model.question.request.TbQuestionRecordSearchRequest;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.unifytask.dto.BatchBuildDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.model.openApi.request.OpenApiGetWorkOrderRequest;
import com.coolcollege.intelligent.model.openApi.request.OpenApiRequest;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author chenyupeng
 * @since 2022/3/29
 */
@Slf4j
@RestController
@RequestMapping("/tmpOpenApi/{enterprise-id}/testShare/")
public class OpenApiController {

    @Autowired
    @Lazy
    private UnifyTaskFcade unifyTaskFcade;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private QuestionRecordService questionRecordService;

    @PostMapping("/getWorkOrder")
    public ResponseResult getWorkOrder(@PathVariable(value = "enterprise-id") String eid,
                                       @RequestBody OpenApiRequest request) {
        if(!verifyMD5(request,eid)){
            return ResponseResult.fail(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        OpenApiGetWorkOrderRequest openApiGetWorkOrderRequest = JSONObject.parseObject(request.getBizContent(),OpenApiGetWorkOrderRequest.class);

        if(openApiGetWorkOrderRequest.getPageNumber() == null || openApiGetWorkOrderRequest.getPageSize() == null){
            return ResponseResult.fail(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());

        TbQuestionRecordSearchRequest recordSearchRequest = new TbQuestionRecordSearchRequest();
        recordSearchRequest.setStoreId(openApiGetWorkOrderRequest.getStoreId());

        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageSize(openApiGetWorkOrderRequest.getPageSize());
        pageRequest.setPageNumber(openApiGetWorkOrderRequest.getPageNumber());

        return ResponseResult.success(questionRecordService.list(eid, recordSearchRequest, pageRequest, UserHolder.getUser()));
    }

    public static boolean verifyMD5(OpenApiRequest request,String eid){
        //签名
        StringBuffer sb = new StringBuffer();
        //用户唯一标识id
        sb.append("userId=").append(request.getUserId()).append("&");
        //企业唯一标识enterpriseId
        sb.append("enterpriseId=").append(eid).append("&");
        sb.append("bizContent=").append(request.getBizContent());

        String md5 = EncryptUtil.oaMd5(sb.toString());

        return md5.equals(request.getSign());
    }
}
