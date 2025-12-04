package com.coolcollege.intelligent.controller.patrolstore;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.safetycheck.request.BigStoreManagerAuditRequest;
import com.coolcollege.intelligent.model.safetycheck.request.SafetyCheckAuditRequest;
import com.coolcollege.intelligent.model.safetycheck.request.SignatureConfirmRequest;
import com.coolcollege.intelligent.model.safetycheck.vo.TbDataColumnCheckHistoryVO;
import com.coolcollege.intelligent.model.safetycheck.vo.TbDataColumnCommentVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.safetycheck.ScSafetyCheckFlowService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wxp
 * @date 2023-08-17 14:28
 */
@Api(tags = "稽核处理")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/patrolStore/safetyCheckFlow")
@ErrorHelper
@Slf4j
public class SafetyCheckFlowController {

    @Resource
    private ScSafetyCheckFlowService scSafetyCheckFlowService;

    @Resource
    private EnterpriseConfigMapper configMapper;



    @ApiOperation("门店伙伴签字确认")
    @PostMapping(path = "/storePartnerSignatureConfirm")
    public ResponseResult<Boolean> storePartnerSignatureConfirm(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                   @RequestBody SignatureConfirmRequest request){
        CurrentUser user =UserHolder.getUser();
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(scSafetyCheckFlowService.storePartnerSignatureConfirm(enterpriseId, request, user, config.getDingCorpId(), config.getAppType()));
    }

    @ApiOperation("大店长审批")
    @PostMapping(path = "/bigStoreManagerAudit")
    public ResponseResult bigStoreManagerAudit(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                   @RequestBody BigStoreManagerAuditRequest request){
        CurrentUser user =UserHolder.getUser();
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(scSafetyCheckFlowService.bigStoreManagerAudit(enterpriseId, request, user, config.getDingCorpId(), config.getAppType()));
    }

    @ApiOperation("食安主管审批")
    @PostMapping(path = "/foodSafetyLeaderAudit")
    public ResponseResult foodSafetyLeaderAudit(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                               @RequestBody SafetyCheckAuditRequest request){
        CurrentUser user =UserHolder.getUser();
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(scSafetyCheckFlowService.foodSafetyLeaderAudit(enterpriseId, request, user, config.getDingCorpId(), config.getAppType()));
    }

    @ApiOperation("点评历史")
    @GetMapping("/listDataColumnCommentHistory")
    public ResponseResult<List<TbDataColumnCommentVO>> listDataColumnCommentHistory(@PathVariable("enterprise-id") String enterpriseId,
                                                                                    @RequestParam(value ="businessId",required = true) Long businessId,
                                                                                    @RequestParam(value ="dataColumnId",required = true) Long dataColumnId){
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        return ResponseResult.success(scSafetyCheckFlowService.listDataColumnCommentHistory(enterpriseId,businessId,dataColumnId));
    }

    @ApiOperation("项的检查历史")
    @GetMapping("/listDataColumnCheckHistory")
    public ResponseResult<TbDataColumnCheckHistoryVO> listDataColumnCheckHistory(@PathVariable("enterprise-id") String enterpriseId,
                                                                                   @RequestParam(value ="businessId",required = true) Long businessId,
                                                                                   @RequestParam(value ="dataColumnId",required = true) Long dataColumnId){
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        return ResponseResult.success(scSafetyCheckFlowService.listDataColumnCheckHistory(enterpriseId,businessId,dataColumnId));
    }

}
