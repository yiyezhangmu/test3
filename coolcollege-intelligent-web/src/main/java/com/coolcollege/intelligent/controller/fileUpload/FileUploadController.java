package com.coolcollege.intelligent.controller.fileUpload;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.FileUtils;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.fileUpload.BaseImage;
import com.coolcollege.intelligent.model.fileUpload.FileUploadParam;
import com.coolcollege.intelligent.model.fileUpload.FileUploadVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.fileUpload.FileUploadService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 文件上传接口
 * 新增接口使用v3
 */
@Api(tags = "文件上传接口")
@Slf4j
@BaseResponse
@RestController
@RequestMapping({"/v2/enterprises/{enterprise-id}/files", "/v3/enterprises/{enterprise-id}/files"})
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;
    @Resource
    private EnterpriseService enterpriseService;
    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Object upload(@PathVariable("enterprise-id") String eid,MultipartFile file) throws IOException{
        if (file.isEmpty()) {
            log.info("文件为空");
        } else {
            log.info("文件属性，名称：{}， 大小：{}，原始文件名：{}", file.getName(), file.getSize(), file.getOriginalFilename());
        }
        if (!FileUtils.checkFileSizeByInputStream(file.getInputStream(), 200)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR.getCode(), "文件大小不能超过200M");
        }
        FileUploadParam fileUploadParam = fileUploadService.uploadFile(file,eid, UserHolder.getUser().getAppType());
        return fileUploadParam;
    }

    @RequestMapping(path = "/uploadSopDoc", method = RequestMethod.POST)
    public FileUploadParam uploadSopDoc(@PathVariable("enterprise-id") String eid,MultipartFile file) throws IOException {

        if (null == file) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "文件为空");
        }else {
            log.info("文件属性，名称：{}， 大小：{}，原始文件名：{}", file.getName(), file.getSize(), file.getOriginalFilename());
        }
        if (!FileUtils.checkPicFileName(file.getOriginalFilename())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR.getCode(), "只能上传：pdf,ppt,doc,docx,xls,xlsx,jpg,jpeg,png 格式文件");
        }
        if (!FileUtils.checkFileSizeByInputStream(file.getInputStream(), 200)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR.getCode(), "文件大小不能超过200M");
        }
        FileUploadParam fileUploadParam = fileUploadService.uploadSopDoc(file,eid, UserHolder.getUser().getAppType(), null);
        return fileUploadParam;
    }




    @RequestMapping(path = "/uploadRecordingFile", method = RequestMethod.POST)
    public FileUploadParam uploadRecordingFile(@PathVariable("enterprise-id") String eid,MultipartFile file) throws IOException {

        if (null == file) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "文件为空");
        }else {
            log.info("文件属性，名称：{}， 大小：{}，原始文件名：{}", file.getName(), file.getSize(), file.getOriginalFilename());
        }
        if (!FileUtils.checkFileSizeByInputStream(file.getInputStream(), 200)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR.getCode(), "文件大小不能超过200M");
        }
        FileUploadParam fileUploadParam = fileUploadService.uploadSopDoc(file,eid, UserHolder.getUser().getAppType(), Constants.RECORDING_FILE_TYPE);
        return fileUploadParam;
    }

    @PostMapping(path = "/uploadBaseImage")
    public FileUploadParam uploadBaseImage(@PathVariable("enterprise-id") String eid,@RequestBody BaseImage image) {
        FileUploadParam fileUploadParam = fileUploadService.uploadBaseImage(image,eid, UserHolder.getUser().getAppType());
        return fileUploadParam;
    }

    /**
     * 上传首页图片
     * @param eid
     * @param file
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "上传首页图片", notes = "上传首页图片")
    @PostMapping(path = "/uploadHomeImage")
    public FileUploadParam uploadHomeImage(@PathVariable("enterprise-id") String eid,MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            log.info("文件为空");
        } else {
            log.info("文件属性，名称：{}， 大小：{}，原始文件名：{}", file.getName(), file.getSize(), file.getOriginalFilename());
        }
        if (!FileUtils.checkFileSizeByInputStream(file.getInputStream(), 200)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR.getCode(), "文件大小不能超过200M");
        }
        DataSourceHelper.reset();
        FileUploadParam fileUploadParam = fileUploadService.uploadHomeImage(eid, file, UserHolder.getUser().getDingCorpId());
        return fileUploadParam;
    }

    @RequestMapping(path = "/uploadWaterMark", method = RequestMethod.POST)
    public Object uploadWaterMark(@PathVariable("enterprise-id") String eId, String waterMarkContent,
        MultipartFile file) {
        if (file.isEmpty()) {
            log.info("文件为空");
        } else {
            log.info("文件属性，名称：{}， 大小：{}，原始文件名：{}，waterMarkContent：{}", file.getName(), file.getSize(),
                file.getOriginalFilename(), waterMarkContent);
        }
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSettings = enterpriseService.getEnterpriseSettings(eId);
        Boolean photoWatermark = enterpriseSettings.getPhotoWatermark();
        FileUploadParam fileUploadParam;
        if (photoWatermark != null && photoWatermark && StringUtils.isNotBlank(waterMarkContent)) {
            String[] waterMarkContents = waterMarkContent.split("\\\\n");
            fileUploadParam = fileUploadService.uploadWaterMark(file, waterMarkContents,eId, UserHolder.getUser().getAppType());
        } else {
            fileUploadParam = fileUploadService.uploadFile(file,eId, UserHolder.getUser().getAppType());
        }
        return fileUploadParam;
    }

    @PostMapping(path = "/uploadBaseImageWithWaterMark")
    public FileUploadParam uploadBaseImageWithWaterMark(@PathVariable("enterprise-id") String eId,
        String waterMarkContent, @RequestBody BaseImage image) {
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSettings = enterpriseService.getEnterpriseSettings(eId);
        Boolean photoWatermark = enterpriseSettings.getPhotoWatermark();
        FileUploadParam fileUploadParam;
        if (photoWatermark != null && photoWatermark && StringUtils.isNotBlank(waterMarkContent)) {
            String[] waterMarkContents = waterMarkContent.split(",");
            fileUploadParam = fileUploadService.uploadBaseImageWithWaterMark(image, waterMarkContents,eId, UserHolder.getUser().getAppType());
        } else {
            fileUploadParam = fileUploadService.uploadBaseImage(image,eId, UserHolder.getUser().getAppType());
        }
        return fileUploadParam;
    }


    @GetMapping(path = "/uploadByMediaIdWithWaterMark")
    public FileUploadParam uploadByMediaIdWithWaterMark(@PathVariable("enterprise-id") String eId,
                                                        String waterMarkContent, @RequestParam(name = "mediaId", required = true) String mediaId,
                                                        @RequestParam(value = "appType", required = false) String appType,
                                                        @RequestParam(value = "appId", required = false) String appId,
                                                        @RequestParam(value = "isWeChatOfficialAccount", required = false) Boolean isWeChatOfficialAccount) {
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSettings = enterpriseService.getEnterpriseSettings(eId);
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eId);
        Boolean photoWatermark = enterpriseSettings.getPhotoWatermark();
        FileUploadParam fileUploadParam;
  
        if (photoWatermark != null && photoWatermark && StringUtils.isNotBlank(waterMarkContent)) {
            String[] waterMarkContents = waterMarkContent.split("\\\\n");
            fileUploadParam = fileUploadService.uploadByMediaIdWithWaterMark(mediaId, waterMarkContents, enterpriseConfigDO.getDingCorpId(), appType, eId, appId, isWeChatOfficialAccount);
        } else {
            fileUploadParam = fileUploadService.uploadByMediaId(mediaId, enterpriseConfigDO.getDingCorpId(), appType, eId, appId, isWeChatOfficialAccount);
        }
        return fileUploadParam;
    }

    @ApiOperation(value = "获取单个文件上传地址", notes = "1、获取文件上传地址； " +
            "2、通过接口返回的上传地址PUT方法Binary格式上传到OSS（Content-Type:application/octet-stream）；" +
            "3、接口返回的访问地址是文件最终上传完成后的访问地址。")
    @ApiImplicitParam(name = "suffix", value = "文件后缀（纯后缀不要点）", required = true)
    @GetMapping(path = "/getUploadUrl")
    public ResponseResult<FileUploadVO> getUploadPath(@PathVariable("enterprise-id") String enterpriseId, String suffix){
        return ResponseResult.success(fileUploadService.getUploadUrl(enterpriseId, suffix, UserHolder.getUser().getAppType()));
    }

    @ApiOperation(value = "获取多个文件上传地址", notes = "1、获取文件上传地址，文件数量必传最多9张； " +
            "2、通过接口返回的上传地址PUT方法Binary格式上传到OSS（Content-Type:application/octet-stream）；" +
            "3、接口返回的访问地址是文件最终上传完成后的访问地址。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "suffix", value = "文件后缀（纯后缀不要点）", required = true),
            @ApiImplicitParam(name = "num", value = "文件数量，必填，最多9张", required = true)
    })
    @GetMapping(path = "/getUploadUrls")
    public ResponseResult<List<FileUploadVO>> getUploadUrls(@PathVariable("enterprise-id") String enterpriseId, String suffix, Integer num){
        return ResponseResult.success(fileUploadService.getUploadUrls(enterpriseId, suffix, num, UserHolder.getUser().getAppType()));
    }

    /**
     * 周大福图片水印上传
     * https://icode.best/i/03299830674710
     * @param eId
     * @param waterMarkContent
     * @param request
     * @return
     */
    @RequestMapping(path = "/zdfUploadWaterMark", method = RequestMethod.POST)
    public Object zdfUploadWaterMark(@PathVariable("enterprise-id") String eId, String waterMarkContent,
                                     HttpServletRequest request) {
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
        // 上传文件的逻辑代码，剩下的代码和之前直接接收MultipartFile一样
        if (file.isEmpty()) {
            log.info("文件为空");
        } else {
            log.info("文件属性，名称：{}， 大小：{}，原始文件名：{}，waterMarkContent：{}", file.getName(), file.getSize(),
                    file.getOriginalFilename(), waterMarkContent);
        }
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSettings = enterpriseService.getEnterpriseSettings(eId);
        Boolean photoWatermark = enterpriseSettings.getPhotoWatermark();
        FileUploadParam fileUploadParam;
        if (photoWatermark != null && photoWatermark && StringUtils.isNotBlank(waterMarkContent)) {
            String[] waterMarkContents = waterMarkContent.split("\\\\n");
            fileUploadParam = fileUploadService.uploadWaterMark(file, waterMarkContents,eId, UserHolder.getUser().getAppType());
        } else {
            fileUploadParam = fileUploadService.uploadFile(file,eId, UserHolder.getUser().getAppType());
        }
        return fileUploadParam;
    }
}
