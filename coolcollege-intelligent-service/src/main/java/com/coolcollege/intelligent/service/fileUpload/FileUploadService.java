package com.coolcollege.intelligent.service.fileUpload;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.BaseException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.common.util.WaterMarkUtil;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dto.EnterpriseConfigExtendInfoDTO;
import com.coolcollege.intelligent.model.fileUpload.BaseImage;
import com.coolcollege.intelligent.model.fileUpload.FileUploadParam;
import com.coolcollege.intelligent.model.fileUpload.FileUploadVO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.wechat.WechatService;
import com.coolcollege.intelligent.util.MediaUploadUtil;
import com.coolstore.base.enums.AppTypeEnum;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by gavin on 15/8/25. 文件上传相关
 */
@Slf4j
@Service
public class FileUploadService {

    @Value("${allow.upload.image.ext}")
    private String auImage;
    @Value("${allow.upload.video.ext}")
    private String auVideo;
    @Value("${allow.upload.audio.ext}")
    private String auAudio;
    @Value("${oss.host}")
    private String OSS_HOST;

    @Autowired
    private OssClientService ossClientService;

    @Autowired
    private EnterpriseConfigApiService enterpriseConfigApiService;

    @Autowired
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;

    @Lazy
    @Resource
    private WechatService wechatService;

    @Value("${isv.url}")
    private String isv_url;

    public FileUploadParam uploadFile(MultipartFile upFile,String eid, String appType) {

        String contentType = upFile.getContentType();
        log.info("文件消息头类型：" + contentType);
        if (StringUtils.isBlank(contentType)) {
            contentType = "application/octet-stream";
        }
        FileUploadParam fup = new FileUploadParam();
        String originalFilename = upFile.getOriginalFilename();
        if (originalFilename.contains("?")) {
            originalFilename = originalFilename.substring(0, originalFilename.indexOf("?"));
        }
        String ext = Files.getFileExtension(originalFilename);
        if (StringUtils.isBlank(ext)) {
            ext = "jpg";
        }
        long size = upFile.getSize();
        // 检查下图片是否是在上传的文件规定之内
        boolean isAllowUpload = isAllowImageUpload(contentType, ext);
        if (!isAllowUpload) {
            throw new BaseException(ErrorCodeEnum.NOT_SUPPORT_FILE_TYPE);
        }
        try {
            String newName = getUploadPath(eid, appType) + System.nanoTime() + "." + ext;
            fup.setExtension(ext);
            fup.setFileSize(size);
            fup.setFileType(upFile.getContentType());
            fup.setFileName(originalFilename);
            fup.setFileNewName(newName);
            fup.setServer(OSS_HOST);
           
            InputStream is = new ByteArrayInputStream(upFile.getBytes());
            if (contentType.contains("image")) {
                fup.setServer(OSS_HOST);
                ossClientService.putObject(newName, is, size, upFile.getContentType());
            } else {
                fup.setServer(OSS_HOST);
                ossClientService.putObjectAsync(newName, is, size, upFile.getContentType());
            }
        } catch (Exception e) {
            log.error("fileUpload upload err, originFileName={}", originalFilename, e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return fup;
    }

    public FileUploadParam uploadSopDoc(MultipartFile upFile,String eid, String appType, String fileExtend) {
        FileUploadParam fup = new FileUploadParam();
        try {
            String originalFilename = upFile.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);//文件扩展名
            //增加默认后缀
            if(StringUtils.isNotBlank(fileExtend)){
                fileType = fileExtend;
            }
            String newName = getUploadPath(eid, appType) + System.nanoTime() + "." + fileType;
            InputStream inputStream = upFile.getInputStream();
            long size = inputStream.available();
            fup.setExtension(fileType);
            fup.setFileSize(size);
            fup.setFileType(fileType);
            fup.setFileName(originalFilename);
            fup.setFileNewName(newName);
            fup.setServer(OSS_HOST);
            ossClientService.putSOPObject(newName, inputStream, size, "application/octet-stream");
        } catch (Exception e) {
            log.error("uploadSopDoc：{}", e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return fup;
    }

    public FileUploadParam uploadBaseImage(BaseImage image,String eid, String appType) {
        FileUploadParam fup = new FileUploadParam();
        try {
            String imageString = image.getFile();
            String suffix = imageString.substring(11, imageString.indexOf(";"));
            // 取出base64
            String newStr = imageString.substring(imageString.indexOf(",") + 1);
            String newName = getUploadPath(eid, appType) + System.nanoTime() + "." + suffix;
            byte[] bytes = new BASE64Decoder().decodeBuffer(newStr);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            long size = inputStream.available();
            fup.setExtension(suffix);
            fup.setFileSize(size);
            fup.setFileType("image");
            fup.setFileNewName(newName);
            fup.setServer(OSS_HOST);
            log.info("uploadBaseImage, start");
            ossClientService.putObject(newName, inputStream, size, "application/octet-stream");
            log.info("uploadBaseImage, return");
        } catch (Exception e) {
            log.error("uploadBaseImage", e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return fup;
    }

    public FileUploadParam uploadBaseImage(String picUrl,String eid, String appType) {
        FileUploadParam fup = new FileUploadParam();
        InputStream inputStream =null;
        try {
            inputStream = getUrlInputStream(picUrl);
            String suffix ="jpg";
            // 取出base64
            String newName = getUploadPath(eid, appType) + System.nanoTime() + "." + suffix;
            long size = inputStream.available();
            fup.setExtension(suffix);
            fup.setFileSize(size);
            fup.setFileType("image");
            fup.setFileNewName(newName);
            fup.setServer(OSS_HOST);
            log.info("uploadBaseImage, start");
            String url = ossClientService.putObject(newName, inputStream);
            fup.setFileUrl(url);
            log.info("uploadBaseImage, return");
        } catch (Exception e) {
            log.error("uploadBaseImage", e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return fup;
    }

    private InputStream  getUrlInputStream(String fileUrl) {
        InputStream is = null;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:55.0) Gecko/20100101 Firefox/55.0");
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(15 * 1000);
            try {
                // 输入流
                is = connection.getInputStream();
            } catch (FileNotFoundException exception) {
                Thread.sleep(2000L);
                is = getUrlInputStream(fileUrl);
                return is;
            }
            if (is.available() == 0) {
                Thread.sleep(2000L);
                is = getUrlInputStream(fileUrl);
                return is;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return is;
    }

    private boolean isAllowImageUpload(String ct, String ext) {

        String allowImageUploadExtString = auImage;

        String allowVideoUploadExtString = auVideo;

        String allowAudioUploadExtString = auAudio;

        String allowFileUploadExtString = auAudio;

        List<String> allowImageUploadExt = Arrays.asList(allowImageUploadExtString.split(","));

        List<String> allowVideoUploadExt = Arrays.asList(allowVideoUploadExtString.split(","));

        List<String> allowFileUploadExt = Arrays.asList(allowFileUploadExtString.split(","));

        if (ct.contains("image")) {
            return Strings.isNullOrEmpty(ext) || allowImageUploadExt.contains(ext.toLowerCase());
        } else if (ct.contains("video")) {
            return Strings.isNullOrEmpty(ext) || allowVideoUploadExt.contains(ext.toLowerCase());
        } else if (ct.contains("audio")) {
            return Strings.isNullOrEmpty(ext) || allowAudioUploadExtString.contains(ext.toLowerCase());
        } else if (ct.contains("file")) {
            return Strings.isNullOrEmpty(ext) || allowFileUploadExt.contains(ext.toLowerCase());
        }

        return true;
    }

    public FileUploadParam uploadWaterMark(MultipartFile upFile, String[] waterMarkContents,String eid, String appType) {
        String contentType = upFile.getContentType();
        log.info("文件消息头类型：" + contentType + "，waterMarkContents:" + JSON.toJSONString(waterMarkContents));
        if (StringUtils.isBlank(contentType)) {
            contentType = "application/octet-stream";
        }
        FileUploadParam fup = new FileUploadParam();
        String originalFilename = upFile.getOriginalFilename();
        if (originalFilename.contains("?")) {
            originalFilename = originalFilename.substring(0, originalFilename.indexOf("?"));
        }
        String ext = Files.getFileExtension(originalFilename);
        if (StringUtils.isBlank(ext)) {
            ext = "jpg";
        }
        // 检查下图片是否是在上传的文件规定之内
        boolean isAllowUpload = isAllowImageUpload(contentType, ext);
        if (!isAllowUpload) {
            throw new BaseException(ErrorCodeEnum.NOT_SUPPORT_FILE_TYPE);
        }
        try {
            String tempName =  System.nanoTime() + "." + ext;
            String newName = getUploadPath(eid, appType) + tempName;
            fup.setExtension(ext);
            fup.setFileSize(upFile.getSize());
            fup.setFileType(upFile.getContentType());
            fup.setFileName(originalFilename);
            fup.setFileNewName(newName);
            fup.setServer(OSS_HOST);
            // 加水印，并生成临时文件
            String tmpImgPath = System.getProperty("user.dir") + File.separator + tempName;
            InputStream is = new ByteArrayInputStream(upFile.getBytes());
            WaterMarkUtil.addWatermark(is, tmpImgPath, waterMarkContents, Color.WHITE,
                new Font("微软雅黑", Font.PLAIN, 32));
            // 水印文件上传
            File waterMarkFile = new File(tmpImgPath);

            InputStream waterMarkIs = new FileInputStream(waterMarkFile);
            if (contentType.contains("image")) {
                fup.setServer(OSS_HOST);
                ossClientService.putObject(newName, waterMarkIs, waterMarkFile.length(), upFile.getContentType());
            } else {
                fup.setServer(OSS_HOST);
                ossClientService.putObjectAsync(newName, waterMarkIs, waterMarkFile.length(), upFile.getContentType());
            }
            log.info("水印图片上传成功，newName={}", newName);
            java.nio.file.Files.delete(Paths.get(tmpImgPath));
            log.info("临时文件删除成功，tmpImgPath={}", tmpImgPath);
        } catch (Exception e) {
            log.error("fileUpload upload err, originFileName={}", originalFilename, e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return fup;
    }

    public FileUploadParam uploadBaseImageWithWaterMark(BaseImage image, String[] waterMarkContents,String eid, String appType) {
        FileUploadParam fup = new FileUploadParam();
        try {
            String imageString = image.getFile();
            String suffix = imageString.substring(11, imageString.indexOf(";"));
            // 取出base64
            String newStr = imageString.substring(imageString.indexOf(",") + 1);
            String tempName =  System.nanoTime() + "." + suffix;
            String newName = getUploadPath(eid, appType) + tempName;
            byte[] bytes = new BASE64Decoder().decodeBuffer(newStr);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            fup.setExtension(suffix);
            fup.setFileSize((long)inputStream.available());
            fup.setFileType("image");
            fup.setFileNewName(newName);
            fup.setServer(OSS_HOST);

            // 加水印，并生成临时文件
            String tmpImgPath = System.getProperty("user.dir") + File.separator + tempName;
            WaterMarkUtil.addWatermark(inputStream, tmpImgPath, waterMarkContents, Color.BLACK,
                new Font("微软雅黑", Font.PLAIN, 20));
            // 水印文件上传
            InputStream waterMarkIs = new FileInputStream(tmpImgPath);
            log.info("uploadBaseImage, start");
            ossClientService.putObject(newName, waterMarkIs, (long)waterMarkIs.available(), "application/octet-stream");
            log.info("uploadBaseImage, return");
            java.nio.file.Files.delete(Paths.get(tmpImgPath));
            log.info("临时文件删除成功，tmpImgPath={}", tmpImgPath);
        } catch (Exception e) {
            log.error("uploadBaseImage", e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return fup;
    }

    public FileUploadParam uploadByMediaIdWithWaterMark(String mediaId, String[] waterMarkContents, String corpId, String appType,String eid, String appId, Boolean isWeChatOfficialAccount) {
        log.info("uploadByMediaIdWithWaterMark媒体id={}, corpId={}, waterMarkContents={}", mediaId, corpId, waterMarkContents);
        FileUploadParam fup = new FileUploadParam();
        try {

            HttpURLConnection httpURLConnection = getHttpURLConnection(corpId, appType, mediaId, appId, isWeChatOfficialAccount);
            log.info("企业微信文件url链接：" + httpURLConnection.getURL().toString());
            InputStream inputStream = httpURLConnection.getInputStream();
            String contentType = httpURLConnection.getContentType();
            log.info("文件消息头类型：" + contentType);
            if (StringUtils.isBlank(contentType)) {
                contentType = "application/octet-stream";
            }
            String fileExt = getFileExt(contentType, httpURLConnection);
            String tempName =  System.nanoTime() + fileExt;
            String newName = getUploadPath(eid, appType) + tempName;
            fup.setExtension(fileExt);
            fup.setFileSize(httpURLConnection.getContentLengthLong());
            fup.setFileType("image");
            fup.setFileNewName(newName);
            fup.setServer(OSS_HOST);

            // 加水印，并生成临时文件
            String tmpImgPath = System.getProperty("user.dir") + File.separator + tempName;
            log.info("临时文件路径，tmpImgPath={}", tmpImgPath);
            WaterMarkUtil.addWatermark(inputStream, tmpImgPath, waterMarkContents, Color.WHITE,
                    new Font("微软雅黑", Font.PLAIN, 64));
            // 水印文件上传
            InputStream waterMarkIs = new FileInputStream(tmpImgPath);
            log.info("uploadBaseImage, start");
            ossClientService.putObject(newName, waterMarkIs, (long)waterMarkIs.available(), contentType);
            log.info("uploadBaseImage, return");
            java.nio.file.Files.delete(Paths.get(tmpImgPath));
            log.info("临时文件删除成功，tmpImgPath={}", tmpImgPath);
        } catch (Exception e) {
            log.error("uploadByMediaIdWithWaterMark: ", e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return fup;
    }

    public FileUploadParam uploadByMediaId(String mediaId , String corpId, String appType,String eid, String appId, Boolean isWeChatOfficialAccount) {
        log.info("uploadByMediaId媒体id={}, corpId={}", mediaId, corpId);
        FileUploadParam fup = new FileUploadParam();
        try {
            HttpURLConnection httpURLConnection = getHttpURLConnection(corpId, appType, mediaId, appId, isWeChatOfficialAccount);
            InputStream inputStream = httpURLConnection.getInputStream();
            String contentType = httpURLConnection.getContentType();
            log.info("文件消息头类型：" + contentType);
            if (StringUtils.isBlank(contentType)) {
                contentType = "application/octet-stream";
            }
            String fileExt = getFileExt(contentType, httpURLConnection);
            String newName = getUploadPath(eid, appType) + System.nanoTime() + fileExt;
            long size = httpURLConnection.getContentLengthLong();
            fup.setExtension(fileExt);
            fup.setFileSize(size);
            fup.setFileType(contentType);
            fup.setFileNewName(newName);
            fup.setServer(OSS_HOST);
            log.info("uploadByMediaId, start");
            ossClientService.putObject(newName, inputStream, size, contentType);
            log.info("uploadByMediaId, return");
        } catch (Exception e) {
            log.error("uploadByMediaId：", e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return fup;
    }

    /**
     * 获取上传url
     * @param enterpriseId 企业id
     * @param suffix 后缀
     * @return FileUploadVO
     */
    public FileUploadVO getUploadUrl(String enterpriseId, String suffix, String appType) {
        //如果图片是没有后缀结尾的，默认添加后缀上传到oss
        String newName = System.nanoTime() + "." + suffix;
        String ossKey = this.getUploadPath(enterpriseId, appType)  + newName;
        log.info("getUploadUrl newName :{}", newName);
        FileUploadVO fileUploadVO = new FileUploadVO();
        fileUploadVO.setFileName(newName);
        fileUploadVO.setOssObjectKey(ossKey);
        fileUploadVO.setFileUrl(OSS_HOST + ossKey);
        fileUploadVO.setUploadUrl(ossClientService.generatePresignedUrl(ossKey).toString());
        log.info("getUploadUrl result :{}", JSONObject.toJSONString(fileUploadVO));
        return fileUploadVO;
    }

    /**
     * 获取多个上传url
     * @param enterpriseId 企业id
     * @param suffix 后缀
     * @param num url个数
     * @return List<FileUploadVO>
     */
    public List<FileUploadVO> getUploadUrls(String enterpriseId, String suffix, Integer num, String appType) {
        if(num > Constants.UPLOAD_FILE_MAX_NUM) {
            throw new BaseException(ErrorCodeEnum.UPLOAD_URL_NUM_ERROR);
        }
        List<FileUploadVO> urls = Lists.newArrayList();
        for (int i = 0; i < num; i++) {
            urls.add(this.getUploadUrl(enterpriseId, suffix, appType));
        }
        return urls;
    }

    public String getUploadPath(String eid, String appType){
        String time = DateUtil.format(new Date(),"yyMM");
        String prefix = AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType) ? appType : "eid";
        if(Constants.PLATFORM_PIC.equals(eid)){
            return Constants.PLATFORM_PIC + "/" + time + "/";
        }
        return prefix + "/"+eid + "/" + time + "/";
    }

    public String getFileExt(String contentType, HttpURLConnection httpURLConnection) throws MimeTypeException {
        String fileExt = null;
        //异常contentType处理
        if("image".equals(contentType)){
            fileExt = FileUtil.getTypeByDisposition(httpURLConnection.getHeaderField("Content-disposition"));
            if(StringUtils.isNotBlank(fileExt)){
                fileExt = "." + fileExt;
            }
        }else {
            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            MimeType imageType = allTypes.forName(contentType);
            fileExt = imageType.getExtension();
        }
        if(StringUtils.isBlank(fileExt)){
            fileExt = ".jpg";
        }
        return fileExt;
    }
    public HttpURLConnection getHttpURLConnection(String corpId, String appType, String mediaId, String appId, Boolean isWeChatOfficialAccount) throws ApiException {
        String accessToken = null;
        EnterpriseConfigExtendInfoDTO enterpriseConfigExtendInfoDTO = enterpriseConfigApiService.getServerDomain(corpId, appType);
        String apiDomain = enterpriseConfigExtendInfoDTO.getApiDomain();
        if (AppTypeEnum.isWxSelfAndPrivateType(appType)) {
            accessToken = enterpriseInitConfigApiService.getAccessToken(corpId, appType);
        } if(StringUtils.isNotBlank(appId) && isWeChatOfficialAccount){
            accessToken = wechatService.getWechatAccessToken(appId);
            apiDomain = "https://api.weixin.qq.com";
        }else {
            ChatService chatService = SpringContextUtil.getBean("chatService", ChatService.class);
            accessToken = chatService.getPyAccessToken(corpId, appType);
        }
        return MediaUploadUtil.downloadMedia(accessToken, mediaId, apiDomain);
    }



    public FileUploadParam uploadHomeImage(String eid, MultipartFile upFile, String dingCorpId) {
        String contentType = upFile.getContentType();
        log.info("文件消息头类型：" + contentType);
        if (StringUtils.isBlank(contentType)) {
            contentType = "application/octet-stream";
        }
        FileUploadParam fup = new FileUploadParam();
        String originalFilename = upFile.getOriginalFilename();
        if (originalFilename.contains("?")) {
            originalFilename = originalFilename.substring(0, originalFilename.indexOf("?"));
        }
        // 获取文件后缀
        String ext = Files.getFileExtension(originalFilename);
        if (StringUtils.isBlank(ext)) {
            ext = "jpg";
        }
        long size = upFile.getSize();
        // 检查下图片是否是在上传的文件规定之内
        boolean isAllowUpload = isAllowImageUpload(contentType, ext);
        if (!isAllowUpload) {
            throw new BaseException(ErrorCodeEnum.NOT_SUPPORT_FILE_TYPE);
        }
        try {
            String newName = "home-pic/" +dingCorpId+ ".jpg";
            fup.setExtension(ext);
            fup.setFileSize(size);
            fup.setFileType(upFile.getContentType());
            fup.setFileName(originalFilename);
            fup.setFileNewName(newName);
            fup.setServer(OSS_HOST);
            InputStream is = new ByteArrayInputStream(upFile.getBytes());
            ossClientService.putObject(newName, is, size, upFile.getContentType());
            //将图片地址存入setting库
            String url = OSS_HOST + newName;
            enterpriseSettingMapper.updateAppHomePagePic(eid, url);
        } catch (Exception e) {
            log.error("fileUpload upload err, originFileName={}", originalFilename, e);
            throw new BaseException(ErrorCodeEnum.INTERNAL_SERVER_ERROR);
        }
        return fup;
    }
}