package com.coolcollege.intelligent.controller.oss;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.model.oss.OssUploadInfoVO;
import com.coolcollege.intelligent.service.fileUpload.OssClientService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: OssController
 * @Description:
 * @date 2023-10-26 10:14
 */
@Api(tags = "OSS上传")
@RestController
@RequestMapping("/oss/{enterprise-id}")
@Slf4j
public class OssController {

    @Value("${oss.access.key.id}")
    private String accessKeyId;
    @Value("${oss.access.key.secret}")
    private String accessKeySecret;
    @Value("${oss.bucket.name}")
    private String bucketName;
    @Value("${oss.host}")
    private String ossHost;
    @Value("${oss.region}")
    private String ossRegion;
    @Value("${region:null}")
    private String region;
    @Value("${oss.sts.endpoint}")
    private String stsEndpoint;
    @Value("${oss.upload.role.arn}")
    private String roleArn;
    @Resource
    private OssClientService ossClientService;
    @Resource
    private RedisUtilPool redisUtilPool;

    @ApiOperation("获取上传token")
    @GetMapping("getOssSecurityToken")
    public ResponseResult<OssUploadInfoVO> getOssToken(@PathVariable("enterprise-id")String enterpriseId, @RequestParam(value = "resource", required = false)String resource){
        String time = DateUtil.format(new Date(),"yyMM");
        String path = "eid/"+enterpriseId + "/" + time + "/";
        if(StringUtils.isNotBlank(resource)){
            path = "eid/" + enterpriseId + "/" + resource + "/";
        }
        String roleSessionName = "yourRoleSessionName";
        Long durationSeconds = 3600L;
        try {
            DefaultProfile.addEndpoint(ossRegion, "Sts", stsEndpoint);
            IClientProfile profile = DefaultProfile.getProfile(ossRegion, accessKeyId, accessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(roleSessionName);
            request.setDurationSeconds(durationSeconds);
            final AssumeRoleResponse response = client.getAcsResponse(request);
            String accessKey = response.getCredentials().getAccessKeyId();
            String accessSecret = response.getCredentials().getAccessKeySecret();
            String securityToken = response.getCredentials().getSecurityToken();
            long expiration = DateUtils.convertUtcTime(response.getCredentials().getExpiration()).getTime();
            OssUploadInfoVO result = new OssUploadInfoVO(enterpriseId, accessKey, accessSecret, securityToken, expiration, ossHost, path, bucketName, ossRegion);
            log.info("上传oss文件获取配置：{}", JSONObject.toJSONString(result));
            return ResponseResult.success(result);
        } catch (ClientException e) {
            log.error("errCode:{}, errMsg:{}, requestId:{}", e.getErrCode(), e.getErrMsg(), e.getRequestId());
        }
        return ResponseResult.success(null);
    }

    @GetMapping("/previewFile")
    public ResponseResult previewFile(@RequestParam("fileUrl") String fileUrl) {
        DataSourceHelper.changeToMy();
        if(isImageByExtension(fileUrl)){
            return ResponseResult.success(fileUrl);
        }
        return ResponseResult.success(ossClientService.getPreviewUrl(fileUrl));
    }

    public static boolean isImageByExtension(String imageUrl) {
        // 获取文件扩展名
        String extension = imageUrl.substring(imageUrl.lastIndexOf('.') + 1).toLowerCase();
        // 定义图片的常见扩展名
        String[] imageExtensions = {"jpg", "jpeg", "png"};
        for (String ext : imageExtensions) {
            if (extension.equals(ext)) {
                return true;
            }
        }
        return false;
    }


}
