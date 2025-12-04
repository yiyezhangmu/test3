package com.coolcollege.intelligent.controller.sop;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.sop.dto.TaskSopClassifyDTO;
import com.coolcollege.intelligent.model.sop.dto.TaskSopClassifySelectDTO;
import com.coolcollege.intelligent.model.sop.param.TaskSopDelParam;
import com.coolcollege.intelligent.model.sop.query.TaskSopQuery;
import com.coolcollege.intelligent.model.sop.vo.TaskSopListVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/2/20 17:24
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/sop")
@BaseResponse
@Slf4j
@Api(tags = "sop文档")
public class SopController {

    @Autowired
    private TaskSopService taskSopService;

    /**
     * 新增sop
     * @param eid
     * @param taskSop
     * @return
     */
    @Deprecated
    @PostMapping("addSop")
    public Boolean addSop(@PathVariable(value = "enterprise-id") String eid,
                          @Valid @RequestBody TaskSopVO taskSop) {
        return taskSopService.insertSop(eid, taskSop);
    }

    /**
     * 新增sop
     * @param eid
     * @param taskSop
     * @return
     */
    @PostMapping("addSopInfo")
    public ResponseResult addSopInfo(@PathVariable(value = "enterprise-id") String eid,
                          @Valid @RequestBody TaskSopVO taskSop) {
        return ResponseResult.success(taskSopService.insertSopInfo(eid, taskSop));
    }

    /**
     * 新增sop
     * @param eid
     * @param taskSop
     * @return
     */
    @PostMapping("addSopList")
    @SysLog(func = "上传文件", opModule = OpModuleEnum.SOP_FILE, opType = OpTypeEnum.INSERT)
    public ResponseResult addSopList(@PathVariable(value = "enterprise-id") String eid,
                           @RequestBody TaskSopListVO taskSop) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(taskSopService.batchInsertSop(eid, taskSop, user));
    }

    /**
     * 新增sop
     * @param eid
     * @param taskSop
     * @return
     */
    @PostMapping("updateSopList")
    @SysLog(func = "编辑", opModule = OpModuleEnum.SOP_FILE, opType = OpTypeEnum.EDIT)
    public ResponseResult updateSopList(@PathVariable(value = "enterprise-id") String eid,
                           @RequestBody TaskSopListVO taskSop) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(taskSopService.updateSopVisibleUser(eid, taskSop, user));
    }

    /**
     * 获取sop列表
     * @param eid
     * @param query
     * @return
     */
    @PostMapping("getSopList")
    public ResponseResult getSopList(@PathVariable(value = "enterprise-id") String eid, @RequestBody TaskSopQuery query) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(taskSopService.selectTaskSopList(eid, query, user));
    }

    // 预览sop文档
    @GetMapping("/previewSop")
    public ResponseResult detail(@PathVariable("enterprise-id") String enterpriseId,
                                 @RequestParam("sopId") Long sopId) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(taskSopService.getSopById(enterpriseId, sopId));
    }

    /**
     * 批量删除sop
     */
    @PostMapping("/batchDeleteSop")
    @OperateLog(operateModule = CommonConstant.Function.SOP, operateType = CommonConstant.LOG_DELETE, operateDesc = "批量删除SOP")
    @SysLog(func = "删除", opModule = OpModuleEnum.SOP_FILE, opType = OpTypeEnum.DELETE)
    public ResponseResult batchDeleteSop(@PathVariable("enterprise-id") String enterpriseId, @RequestBody TaskSopDelParam taskSopDelParam){
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        taskSopService.batchDeleteSop(enterpriseId, taskSopDelParam.getSopIdList(), user);
        return ResponseResult.success(true);
    }

    /**
     * 新增sop分类
     * @param eid
     * @param classify
     * @return
     */
    @PostMapping("addSopClassify")
    public Boolean addSopClassify(@PathVariable(value = "enterprise-id") String eid,
                                  @RequestBody TaskSopClassifyDTO classify) {
        return taskSopService.addSopClassify(eid, classify.getClassifyName());
    }

    /**
     * 修改sop分类
     * @param eid
     * @param classify
     * @return
     */
    @PostMapping("updateSopClassify")
    public Boolean updateSopClassify(@PathVariable(value = "enterprise-id") String eid,
                                     @RequestBody TaskSopClassifyDTO classify) {
        return taskSopService.updateSopClassify(eid, classify);
    }

    /**
     * 获取分类列表
     * @param eid
     * @return
     */
    @GetMapping("getSopClassifyList")
    public List<TaskSopClassifySelectDTO> updateSopClassify(@PathVariable(value = "enterprise-id") String eid) {
        DataSourceHelper.changeToMy();
        return taskSopService.selectSopClassifyList(eid);
    }

    /**
     * 获取SOP分类列表
     * @param enterpriseId
     * @return
     */
    @GetMapping("/selectAllCategory")
    public ResponseResult selectAllCategory(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.changeToMy();
        List<String> categoryList = taskSopService.selectAllCategory(enterpriseId);
        return ResponseResult.success(categoryList);
    }


    @PostMapping("addSupervisionSopList")
    @ApiOperation("督导助手新增附件接口")
    public ResponseResult addSupervisionSopList(@PathVariable(value = "enterprise-id") String eid,
                                     @RequestBody TaskSopListVO taskSop) {
        CurrentUser user = UserHolder.getUser();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(taskSopService.batchInsertSupervisionSop(eid, taskSop, user));
    }

}
