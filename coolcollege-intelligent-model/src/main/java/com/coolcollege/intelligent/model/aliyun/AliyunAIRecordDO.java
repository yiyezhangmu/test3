package com.coolcollege.intelligent.model.aliyun;

import lombok.Data;

/**
 * @author 王春辉
 * 阿里云抓拍记录
 */
@Data
public class AliyunAIRecordDO {
    private Integer id;
    private String storeId;
    private String  faceId;
    private String  aliyunCorpId;
    private String dataSourceId;
    private String shotTime;
    private String genderCode;
    private String maxAge;
    private String minAge;
    private String capStyle;
    private String hairStyle;
    private String picUrlPath;
    private String targetPicUrlPath;
    private String  leftTopX;
    private String leftTopY;
    private String rightBottomX;
    private String rightBottomY;

}
