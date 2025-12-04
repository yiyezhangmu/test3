package com.coolcollege.intelligent.model.oss;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: OssUploadInfoVO
 * @Description:
 * @date 2023-10-26 14:05
 */
@Data
public class OssUploadInfoVO {

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("ak")
    private String accessKeyId;

    @ApiModelProperty("sk")
    private String accessKeySecret;

    @ApiModelProperty("token")
    private String securityToken;

    @ApiModelProperty("过期时间")
    private long expiration;

    @ApiModelProperty("host")
    private String host;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("路径")
    private String bucketName;

    @ApiModelProperty("区域")
    private String region;

    @ApiModelProperty("policy")
    private String policy;

    public OssUploadInfoVO(String enterpriseId, String accessKeyId, String accessKeySecret, String securityToken, long expiration, String host, String path, String bucketName, String region) {
        this.enterpriseId = enterpriseId;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.securityToken = securityToken;
        this.expiration = expiration;
        this.host = host;
        this.path = path;
        this.bucketName = bucketName;
        this.region = region;
    }
}
