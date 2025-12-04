package com.coolcollege.intelligent.controller.fsGroup;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.fsGroup.query.*;
import com.coolcollege.intelligent.model.fsGroup.request.*;
import com.coolcollege.intelligent.model.fsGroup.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.fsGroup.FsGroupService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v3/enterprises/{enterprise-id}/fsGroup")
@Api(tags = "群运营")
public class FsGroupController {


    @Resource
    private FsGroupService fsGroupService;

    @PostMapping("/addGroup")
    @ApiOperation(value = "新建飞书群")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_ADD, operateDesc = "新建飞书群")
    public ResponseResult addFsGroup(@PathVariable("enterprise-id") String eid, @RequestBody FsGroupAddRequest request) {
        log.info("addFsGroup request:{}", request);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        fsGroupService.addFsGroup(eid, request, user);
        return ResponseResult.success();
    }

    @PostMapping("/deletedGroup")
    @ApiOperation(value = "删除飞书群")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_DELETE, operateDesc = "解散飞书群")
    public ResponseResult deletedGroup(@PathVariable("enterprise-id") String eid,@RequestParam String chatId) {
        log.info("deletedGroup chatId:{}", chatId);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        fsGroupService.deletedGroup(eid, chatId, user);
        return ResponseResult.success();
    }


//    @PostMapping("/addGroupTopMsg")
//    @ApiOperation(value = "新建群置顶消息")
//    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_ADD, operateDesc = "新建群置顶消息")
//    public ResponseResult addGroupTopMsg(@PathVariable("enterprise-id") String eid, @RequestBody FsGroupTopMenuRequest request) {
//        log.info("addGroupTopMsg request:{}", request);
//        DataSourceHelper.changeToMy();
//        CurrentUser user = UserHolder.getUser();
//        fsGroupService.addGroupTopMsg(eid, request, user);
//        return ResponseResult.success();
//    }

    @PostMapping("/addGroupTopMenu")
    @ApiOperation(value = "新建群置顶")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_ADD, operateDesc = "新建群顶部菜单")
    public ResponseResult addGroupTopMenu(@PathVariable("enterprise-id") String eid,@Valid @RequestBody FsGroupTopMenuRequest request) {
        log.info("addGroupTopMenu request:{}", request);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        fsGroupService.addGroupTopMenu(eid, request, user);
        return ResponseResult.success();
    }

    @PostMapping("/addGroupNotice")
    @ApiOperation(value = "新建群公告")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_ADD, operateDesc = "新建群公告")
    public ResponseResult addGroupNotice(@PathVariable("enterprise-id") String eid,@Valid @RequestBody FsGroupNoticeRequest request) {
        log.info("addGroupNotice request:{}", request);
        CurrentUser user = UserHolder.getUser();
        fsGroupService.addGroupNotice(eid, request, user);
        return ResponseResult.success();
    }

    @PostMapping("/getFsGroupList")
    @ApiOperation(value = "获取群管理列表")
    public ResponseResult<PageInfo<FsGroupVO>> getFsGroupList(@PathVariable("enterprise-id") String eid, @RequestBody FsGroupQuery query) {
        log.info("getFsGroup request:{}", query);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getFsGroupList(eid, query));
    }

    @PostMapping("/getFsGroupSceneList")
    @ApiOperation(value = "获取群应用管理信息")
    public ResponseResult<PageInfo<FsGroupSceneVO>> getFsGroupSceneList(@PathVariable("enterprise-id") String eid, @RequestBody FsGroupSceneQuery query) {
        log.info("getFsGroupSceneList request:{}", query);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getFsGroupSceneList(eid, query));
    }

    @PostMapping("/getFsGroupByScene")
    @ApiOperation(value = "获取群应用管理群列表")
    public ResponseResult<PageInfo<FsGroupVO>> getFsGroupByScene(@PathVariable("enterprise-id") String eid, @RequestBody FsGroupSceneMappingQuery query) {
        log.info("getFsGroupByScene request:{}", query);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getFsGroupByScene(eid, query));
    }


    @PostMapping("/getFsGroupNoticeList")
    @ApiOperation(value = "获取群公告列表")
    public ResponseResult<PageInfo<FsGroupNoticeVO>> getFsGroupNoticeList(@PathVariable("enterprise-id") String eid, @RequestBody FsGroupNoticeQuery query) {
        log.info("getFsGroupNoticeList request:{}", query);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getFsGroupNoticeList(eid, query));
    }

    @PostMapping("/getFsGroupTopMenuList")
    @ApiOperation(value = "获取群置顶列表")
    public ResponseResult<PageInfo<FsGroupTopMenuVO>> getFsGroupTopMenuList(@PathVariable("enterprise-id") String eid, @RequestBody FsGroupTopMenuQuery query) {
        log.info("getFsGroupTopMenuList request:{}", query);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getFsGroupTopMenuList(eid, query));
    }

    @PostMapping("/addGroupMenu")
    @ApiOperation(value = "新建群菜单")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_ADD, operateDesc = "新建群菜单")
    public ResponseResult addGroupMenu(@PathVariable("enterprise-id") String eid,@Valid @RequestBody FsGroupMenuRequest request) {
        log.info("addGroupMenu request:{}", request);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        fsGroupService.addGroupMenu(eid, request, user);
        return ResponseResult.success();
    }

    @PostMapping("/getGroupMenuList")
    @ApiOperation("获取群菜单列表")
    public ResponseResult<PageInfo<FsGroupMenuVO>> getGroupMenuList(@PathVariable("enterprise-id") String eid, @RequestBody FsGroupMenuQuery query) {
        log.info("getGroupMenuList request:{}", query);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getGroupMenuList(eid, query));
    }

    @PostMapping("/deletedGroupNotice")
    @ApiOperation(value = "删除群公告")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除群公告")
    public ResponseResult deletedGroupNotice(@PathVariable("enterprise-id") String eid,@RequestParam Long noticeId) {
        log.info("deletedGroupNotice id:{}", noticeId);
        DataSourceHelper.changeToMy();
        fsGroupService.deletedGroupNotice(eid, noticeId);
        return ResponseResult.success();
    }


    @PostMapping("/deletedGroupTopMenu")
    @ApiOperation(value = "删除群置顶")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除群置顶")
    public ResponseResult deletedGroupTopMenu(@PathVariable("enterprise-id") String eid,@RequestParam Long topMenuId) {
        log.info("deletedGroupTopMenu id:{}", topMenuId);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        fsGroupService.deletedGroupTopMenu(eid, topMenuId,user);
        return ResponseResult.success();
    }

    @PostMapping("/deletedGroupMenu")
    @ApiOperation(value = "删除群菜单")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_DELETE, operateDesc = "删除群菜单")
    public ResponseResult deletedGroupMenu(@PathVariable("enterprise-id") String eid,@RequestParam Long menuId) {
        log.info("deletedGroupMenu id:{}", menuId);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        fsGroupService.deletedGroupMenu(eid, menuId,user);
        return ResponseResult.success();
    }

    @PostMapping("/updateGroup")
    @ApiOperation(value = "编辑群")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_UPDATE, operateDesc = "编辑飞书群")
    public ResponseResult updateGroup(@PathVariable("enterprise-id") String eid,@Valid @RequestBody FsGroupAddRequest request) {
        log.info("updateGroup request:{}", request);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        fsGroupService.updateGroup(eid, request,user);
        return ResponseResult.success();
    }

    @PostMapping("/updateGroupMenu")
    @ApiOperation(value = "编辑群菜单")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_UPDATE, operateDesc = "编辑群菜单")
    public ResponseResult updateGroupMenu(@PathVariable("enterprise-id") String eid,@Valid @RequestBody FsGroupMenuRequest request) {
        log.info("updateGroupMenu request:{}", request);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        fsGroupService.updateGroupMenu(eid, request,user);
        return ResponseResult.success();
    }

    @PostMapping("/updateGroupTopMenu")
    @ApiOperation(value = "编辑群置顶")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_UPDATE, operateDesc = "编辑群置顶")
    public ResponseResult updateGroupTopMenu(@PathVariable("enterprise-id") String eid,@Valid @RequestBody FsGroupTopMenuRequest request) {
        log.info("updateGroupTopMenu request:{}", request);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        fsGroupService.updateGroupTopMenu(eid, request,user);
        return ResponseResult.success();
    }

    @PostMapping("/updateGroupNotice")
    @ApiOperation(value = "编辑群公告")
    @OperateLog(operateModule = CommonConstant.Function.USER, operateType = CommonConstant.LOG_UPDATE, operateDesc = "编辑群公告")
    public ResponseResult updateGroupNotice(@PathVariable("enterprise-id") String eid,@Valid @RequestBody FsGroupNoticeRequest request) {
        log.info("updateGroupNotice request:{}", request);
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        fsGroupService.updateGroupNotice(eid, request,user);
        return ResponseResult.success();
    }

    @GetMapping("/getGroupDetail")
    @ApiOperation(value = "获取群详情")
    public ResponseResult<FsGroupVO> getFsGroupDetail(@PathVariable("enterprise-id") String eid, @RequestParam("id")Long groupId) {
        log.info("getGroupDetail request:{}", groupId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getFsGroupDetail(eid,groupId));
    }

    @GetMapping("/getGroupNoticeDetail")
    @ApiOperation(value = "获取群公告详情")
    public ResponseResult<FsGroupNoticeVO> getGroupNoticeDetail(@PathVariable("enterprise-id") String eid, @RequestParam("noticeId")Long noticeId) {
        log.info("getGroupNoticeDetail request:{}", noticeId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getGroupNoticeDetail(eid,noticeId));
    }

    @GetMapping("/getGroupTopMenuDetail")
    @ApiOperation(value = "获取群置顶详情")
    public ResponseResult<FsGroupTopMenuVO> getGroupTopMenuDetail(@PathVariable("enterprise-id") String eid, @RequestParam("topMenuId")Long topMenuId) {
        log.info("getGroupTopMenuDetail request:{}", topMenuId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getGroupTopMenuDetail(eid,topMenuId));
    }

    @GetMapping("/getGroupMenuDetail")
    @ApiOperation(value = "获取群菜单详情")
    public ResponseResult<FsGroupMenuVO> getGroupMenuDetail(@PathVariable("enterprise-id") String eid, @RequestParam("menuId")Long menuId) {
        log.info("getGroupMenuDetail request:{}", menuId);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getGroupMenuDetail(eid,menuId));
    }

    @PostMapping("/uploadFsImg")
    @ApiOperation(value = "上传飞书图片")
    public ResponseResult<String> uploadFsImg(@PathVariable("enterprise-id") String eid, @RequestParam("file") MultipartFile img) {
        log.info("uploadFsImg request:{}", img);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.uploadFsImg(eid, img));
    }

    @PostMapping(value = "/downloadFsImg",produces ="application/octet-stream")
    @ApiOperation("下载飞书图片")
    public ResponseResult<byte[]> downloadFsImg(@PathVariable("enterprise-id") String eid,@RequestParam("imgKey") String imgKey){
        log.info("downloadFsImg request:{}", imgKey);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.downloadFsImg(eid, imgKey));
    }

    @PostMapping("/getFsToken")
    @ApiOperation(value = "获取飞书token")
    public ResponseResult<String> getFsToken(@PathVariable("enterprise-id") String eid) {
        log.info("getFsToken request:{}", eid);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.getFsToken(eid));
    }


    @PostMapping ("/deleteSceneGroup")
    @ApiOperation(value = "群应用管理移除群")
    public ResponseResult<Boolean> deleteSceneGroup(@PathVariable("enterprise-id") String eid, @RequestBody SceneGroupIdRequest request){
        log.info("deleteSceneGroup request:{}",eid,request);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.deleteSceneGroup(eid,request.getIds()));
    }


    @PostMapping("/addSceneForGroups")
    @ApiOperation(value = "批量配置群场景")
    public ResponseResult<Boolean> addSceneForGroups(@PathVariable("enterprise-id") String eid, @RequestBody SceneGroupIdRequest request){
        log.info("addSceneForGroups request:{}",eid,request);
        DataSourceHelper.changeToMy();
        return ResponseResult.success(fsGroupService.addSceneForGroups(eid,request));
    }



}
