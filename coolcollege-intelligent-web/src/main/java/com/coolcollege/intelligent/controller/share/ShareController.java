package com.coolcollege.intelligent.controller.share;

import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.ResponseCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.share.dto.TaskShareInsertDTO;
import com.coolcollege.intelligent.service.share.ShareService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/v2/enterprises/{enterprise-id}/share")
public class ShareController {
    @Resource
    private ShareService shareService;

    /**
     * 单个分享
     * @param eId
     * @param taskShareInsertDTO
     * @return
     */
    @PostMapping("/singleShare")
    @OperateLog(operateModule = CommonConstant.Function.SHARE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "单个分享")
    public ResponseResult singleShare(@PathVariable("enterprise-id") String eId, @RequestBody TaskShareInsertDTO taskShareInsertDTO){

        DataSourceHelper.changeToMy();
        Boolean isSuccess = shareService.singleShare(eId, taskShareInsertDTO);
        if(isSuccess){
          return  new ResponseResult(ResponseCodeEnum.SUCCESS.getCode(),"分享成功",true);
        }
        return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(), "分享失败");
    }

    /**
     * 单个新标准陈列分享
     * @param eId
     * @param taskShareInsertDTO
     * @return
     */
    @PostMapping("/tbDisplayShare")
    @OperateLog(operateModule = CommonConstant.Function.SHARE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "单个新标准陈列分享")
    public ResponseResult tbDisplayShare(@PathVariable("enterprise-id") String eId, @RequestBody TaskShareInsertDTO taskShareInsertDTO){

        DataSourceHelper.changeToMy();
        Boolean isSuccess = shareService.singleShare(eId, taskShareInsertDTO);
        if(isSuccess){
            return  new ResponseResult(ResponseCodeEnum.SUCCESS.getCode(),"分享成功",true);
        }
        return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(), "分享失败");
    }

    /**
     * 批量分享
     * @param eId
     * @param taskShareInsertDTO
     * @return
     */
    @PostMapping("/batchShare")
    @OperateLog(operateModule = CommonConstant.Function.SHARE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "批量分享")
    public ResponseResult batchShare(@PathVariable("enterprise-id")String eId,@RequestBody TaskShareInsertDTO taskShareInsertDTO){
        DataSourceHelper.changeToMy();
        Map<String,Object> result = (Map<String, Object>) shareService.batchShare(eId, taskShareInsertDTO);
        if((Boolean)result.get("isSuccess")){
            return  new ResponseResult(ResponseCodeEnum.SUCCESS.getCode(),"分享成功",true);
        }
        return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(), (String) result.get("message"));
    }

    /**
     * 获取圈子列表
     * @param eId
     * @return
     */
    @GetMapping("/getShareList")
    public ResponseResult getShareList(@PathVariable("enterprise-id") String eId,@RequestParam("page_size")Integer pageSize, @RequestParam("page_num")Integer pageNum,@RequestParam(name = "search_key",required = false,defaultValue = "")String searchKey){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(shareService.getShareList(eId,null,pageSize,pageNum,searchKey));
    }

    /**
     * 获取分享详情
     * @param eId
     * @param shareId
     * @return
     */
    @GetMapping("/getShareDetail")
    public ResponseResult getShareDetail(@PathVariable("enterprise-id")String eId,@RequestParam("share_id")String shareId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(shareService.getShareDetail(eId,shareId));
    }

    @PostMapping("/patrolStoreShare")
    @OperateLog(operateModule = CommonConstant.Function.SHARE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "分享")
    public ResponseResult patrolStoreShare(@PathVariable("enterprise-id") String eId,@RequestBody TaskShareInsertDTO taskShareInsertDTO){
        DataSourceHelper.changeToMy();
        shareService.patrolStoreShare(eId,taskShareInsertDTO);
        return ResponseResult.success("分享成功");
    }

    @ApiOperation("圈子分享")
    @PostMapping("/circlesShare")
    @OperateLog(operateModule = CommonConstant.Function.SHARE, operateType = CommonConstant.LOG_UPDATE, operateDesc = "分享")
    public ResponseResult circlesShare(@PathVariable("enterprise-id") String eId,@RequestBody TaskShareInsertDTO taskShareInsertDTO){
        DataSourceHelper.changeToMy();
        shareService.circlesShare(eId,taskShareInsertDTO);
        return ResponseResult.success("分享成功");
    }


}
