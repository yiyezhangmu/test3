package com.coolcollege.intelligent.controller.share;

import cn.hutool.json.JSONUtil;
import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.unifytask.query.ShareSubQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayHistoryService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskDisplayService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author byd
 */
@Api(tags = "分享")
@ErrorHelper
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/shareInfo")
public class ShareInfoController {

    @Resource
    private UnifyTaskDisplayService unifyTaskDisplayService;

    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private RedisUtilPool redisUtilPool;

    @Resource
    private TbDisplayService tbDisplayService;

    @Resource
    private TbDisplayHistoryService tbDisplayHistoryService;

    /**
     * 详情
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    @ApiOperation("陈列子任务详情")
    @GetMapping(path = "/display/sub/detail")
    public ResponseResult getDisplaySubDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                              @Validated ShareSubQuery query) {

        String subTaskId = redisUtilPool.getString(UnifyTaskConstant.TASK_SHARE + query.getKey());
        if (StringUtils.isBlank(subTaskId)) {
            throw new ServiceException(ErrorCodeEnum.SHARE_KEY_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        //切到指定的库
        // FIXME 去除CurrentUser，切库改成根据eid切库 byd
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        CurrentUser currentUser = new CurrentUser();
        currentUser.setDbName(enterpriseConfigDO.getDbName());
        UserHolder.setUser(JSONUtil.toJsonStr(currentUser));
        return ResponseResult.success(unifyTaskDisplayService.getDisplaySubDetail(enterpriseId, Long.valueOf(subTaskId), Constants.SYSTEM_USER_ID, null));
    }

    /**
     * 根据子任务id查询任务记录详情和检查项列表
     *
     * @return
     */
    @ApiOperation("根据子任务id查询任务记录详情和检查项列表")
    @GetMapping(path = "/display/detail")
    public ResponseResult displayDetail(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                        @Validated ShareSubQuery query) {
        String subTaskId = redisUtilPool.getString(UnifyTaskConstant.TASK_SHARE + query.getKey());
        if (StringUtils.isBlank(subTaskId)) {
            throw new ServiceException(ErrorCodeEnum.SHARE_KEY_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        //切到指定的库
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        CurrentUser currentUser = new CurrentUser();
        currentUser.setDbName(enterpriseConfigDO.getDbName());
        UserHolder.setUser(JSONUtil.toJsonStr(currentUser));
        return ResponseResult.success(tbDisplayService.detail(enterpriseId, Constants.SYSTEM_USER_ID, Long.valueOf(subTaskId)));
    }

    /**
     * 记录操作历史
     *
     * @return
     */
    @ApiOperation("记录操作历史")
    @GetMapping(path = "/display/listHistoryByTaskSubId")
    public ResponseResult listHistoryByTaskSubId(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                                 @Validated ShareSubQuery query) {
        String subTaskId = redisUtilPool.getString(UnifyTaskConstant.TASK_SHARE + query.getKey());
        if (StringUtils.isBlank(subTaskId)) {
            throw new ServiceException(ErrorCodeEnum.SHARE_KEY_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        //切到指定的库
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        CurrentUser currentUser = new CurrentUser();
        currentUser.setDbName(enterpriseConfigDO.getDbName());
        UserHolder.setUser(JSONUtil.toJsonStr(currentUser));
        return ResponseResult.success(tbDisplayHistoryService.listHistoryByTaskSubId(enterpriseId, Long.valueOf(subTaskId)));
    }
}
